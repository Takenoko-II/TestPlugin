package com.gmail.subnokoii78.testplugin.system.field;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.database.SqliteDatabase;
import com.gmail.subnokoii78.tplcore.vector.BlockPositionBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@NullMarked
public class GameFieldRestorer extends SqliteDatabase {
    private static final String X = "x";

    private static final String Y = "y";

    private static final String Z = "z";

    private static final String BLOCK_DATA = "block_data";

    private record BlockModificationRecord(BlockPositionBuilder position, BlockData blockData) {}

    private static final Map<World, GameFieldRestorer> caches = new HashMap<>();

    private final World world;

    private final List<BlockModificationRecord> batches = new ArrayList<>();

    public GameFieldRestorer(World world) {
        super(TPLCore.getPlugin().getDataPath() + "/GameFieldRestorer_" + world.getName() + ".db");

        if (caches.containsKey(world)) {
            throw new IllegalStateException();
        }

        this.world = world;
        caches.put(world, this);
    }

    public World getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameFieldRestorer that = (GameFieldRestorer) o;
        return Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world);
    }

    public void open() {
        if (!file.exists()) {
            final boolean successful;
            try {
                successful = file.createNewFile();
            }
            catch (IOException e) {
                throw new IllegalStateException("dbファイルの作成に失敗しました: " + file, e);
            }
            if (successful) {
                TPLCore.getPlugin().getComponentLogger().info(Component.text(
                    "dbファイルの作成に成功しました"
                ));
            }
            else {
                throw new IllegalStateException("dbファイルの作成に失敗しました: " + file);
            }
        }

        connect();
        create();
    }

    public void close() {
        flush();
        restore();
        disconnect();
    }

    public void restore() {
        load();
        clear();
    }

    public void batch(BlockPositionBuilder position, BlockData blockData) {
        batches.add(new BlockModificationRecord(position, blockData));
    }

    public int flush() {
        final int c = save(batches);
        batches.clear();
        return c;
    }

    private void create() {
        final String sql = String.format(
            """
            CREATE TABLE IF NOT EXISTS %s (
                %s int NOT NULL,
                %s int NOT NULL,
                %s int NOT NULL,
                %s TEXT NOT NULL,
                PRIMARY KEY (x, y, z)
            );
            """,
            world.getName(),
            X, Y, Z,
            BLOCK_DATA
        );

        try (final Statement statement = getConnection().createStatement()) {
            statement.execute(sql);
        }
        catch (SQLException e) {
            throw new IllegalStateException("テーブルの作成に失敗しました: ", e);
        }
    }

    private boolean exists(BlockPositionBuilder position) {
        final String sql = String.format(
            """
            SELECT 1 FROM %s
            WHERE %s=? AND %s=? AND %s=?
            """,
            world.getName(),
            X, Y, Z
        );

        try (final PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, position.x());
            preparedStatement.setInt(2, position.y());
            preparedStatement.setInt(3, position.z());

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }

    private int save(List<BlockModificationRecord> records) {
        final String sql = String.format(
            """
            INSERT OR IGNORE INTO %s(%s, %s, %s, %s) VALUES (?, ?, ?, ?)
            """,
            world.getName(),
            X, Y, Z, BLOCK_DATA
        );

        final int[] result;

        try (final PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            getConnection().setAutoCommit(false);

            for (final BlockModificationRecord record : records) {
                preparedStatement.setInt(1, record.position.x());
                preparedStatement.setInt(2, record.position.y());
                preparedStatement.setInt(3, record.position.z());
                preparedStatement.setString(4, record.blockData.getAsString());
                preparedStatement.addBatch();
            }

            result = preparedStatement.executeBatch();

            getConnection().commit();
            getConnection().setAutoCommit(true);
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }

        return Arrays.stream(result).sum();
    }

    private void load() {
        final String sql = String.format(
            """
            SELECT %s, %s, %s, %s FROM %s
            """,
            X, Y, Z, BLOCK_DATA,
            world.getName()
        );

        try (final Statement statement = getConnection().createStatement(); final ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final BlockPositionBuilder position = new BlockPositionBuilder(
                    resultSet.getInt(X),
                    resultSet.getInt(Y),
                    resultSet.getInt(Z)
                );
                final BlockData blockData = Bukkit.createBlockData(resultSet.getString(BLOCK_DATA));

                position.toBlock(world).setBlockData(blockData);
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }

    private void clear() {
        final String sql = String.format(
            """
            DELETE FROM %s
            """,
            world.getName()
        );

        try (final Statement statement = getConnection().createStatement()) {
            statement.execute(sql);
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public Map<BlockPositionBuilder, String> get() {
        final String sql = String.format(
            """
            SELECT %s, %s, %s, %s FROM %s
            """,
            X, Y, Z, BLOCK_DATA,
            world.getName()
        );

        final Map<BlockPositionBuilder, String> data = new HashMap<>();

        try (final Statement statement = getConnection().createStatement(); final ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final BlockPositionBuilder position = new BlockPositionBuilder(
                    resultSet.getInt(X),
                    resultSet.getInt(Y),
                    resultSet.getInt(Z)
                );
                final String serializedBlockData = resultSet.getString(BLOCK_DATA);
                data.put(position, serializedBlockData);
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }

        return data;
    }
}

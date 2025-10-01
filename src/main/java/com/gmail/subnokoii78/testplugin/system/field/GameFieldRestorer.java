package com.gmail.subnokoii78.testplugin.system.field;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.database.SqliteDatabase;
import com.gmail.subnokoii78.tplcore.vector.BlockPositionBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GameFieldRestorer extends SqliteDatabase {
    private static final String TABLE_NAME = "blocks";

    private static final String WORLD_KEY = "world_key";

    private static final String X = "x";

    private static final String Y = "y";

    private static final String Z = "z";

    private static final String BLOCK_DATA = "block_data";

    private record BlockModificationRecord(World world, BlockPositionBuilder position, BlockData blockData) {}

    private final List<BlockModificationRecord> batches = new ArrayList<>();

    public GameFieldRestorer(String path) {
        super(path);
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
        load();
        clear();
        disconnect();
    }

    public void batch(World world, BlockPositionBuilder position, BlockData blockData) {
        batches.add(new BlockModificationRecord(world, position, blockData));
    }

    public void flush() {
        save(batches);
        batches.clear();
    }

    private void create() {
        final String sql = String.format(
            """
            CREATE TABLE IF NOT EXISTS %s (
                %s TEXT NOT NULL,
                %s int NOT NULL,
                %s int NOT NULL,
                %s int NOT NULL,
                %s TEXT NOT NULL,
                PRIMARY KEY (world, x, y, z)
            );
            """,
            TABLE_NAME,
            WORLD_KEY,
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

    private boolean exists(World world, BlockPositionBuilder position) {
        final String sql = String.format(
            """
            SELECT 1 FROM %s
            WHERE %s=? AND %s=? AND %s=? AND %s=?
            """,
            TABLE_NAME,
            WORLD_KEY,
            X, Y, Z
        );

        try (final PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, world.getName());
            preparedStatement.setInt(2, position.x());
            preparedStatement.setInt(3, position.y());
            preparedStatement.setInt(4, position.z());

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }

    private void save(List<BlockModificationRecord> records) {
        final String sql = String.format(
            """
            INSERT OR IGNORE INTO %s(%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)
            """,
            TABLE_NAME,
            WORLD_KEY, X, Y, Z, BLOCK_DATA
        );

        try (final PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            for (final BlockModificationRecord record : records) {
                preparedStatement.setString(1, record.world.getKey().asString());
                preparedStatement.setInt(2, record.position.x());
                preparedStatement.setInt(3, record.position.y());
                preparedStatement.setInt(4, record.position.z());
                preparedStatement.setString(5, record.blockData.getAsString());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }

    private void load() {
        final String sql = String.format(
            """
            SELECT %s, %s, %s, %s, %s FROM %s
            """,
            WORLD_KEY, X, Y, Z, BLOCK_DATA,
            TABLE_NAME
        );

        try (final Statement statement = getConnection().createStatement(); final ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final String worldKeyStr = resultSet.getString(WORLD_KEY);
                final int x = resultSet.getInt(X);
                final int y = resultSet.getInt(Y);
                final int z = resultSet.getInt(Z);
                final String blockDataStr = resultSet.getString(BLOCK_DATA);

                final NamespacedKey key = NamespacedKey.fromString(worldKeyStr);

                if (key == null) {
                    throw new IllegalStateException("ワールドのキーを解決できませんでした: " + worldKeyStr);
                }

                final World world = Bukkit.getWorld(key);

                if (world == null) {
                    throw new IllegalStateException("ワールド '" + key + "' は存在しません");
                }

                final BlockPositionBuilder position = new BlockPositionBuilder(x, y, z);
                final BlockData blockData = Bukkit.createBlockData(blockDataStr);
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
            TABLE_NAME
        );

        try (final Statement statement = getConnection().createStatement()) {
            statement.execute(sql);
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }
}

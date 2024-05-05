# TestPlugin
ぷらぐいーん

[これ](target/TestPlugin-1.0-SNAPSHOT.jar)をpluginsフォルダにぶち込むと使えるよ

## Scoreboard Objectives
- `plugin.events.player.left_click`
<br>プレイヤーが左クリックを行うと1増加する。

- `plugin.scheduler.tick_listener`
<br>1以上の値をプレイヤーに持たせると、1tick後にプラグインの処理が起動する。

## Entity Tags
- `plugin.permission.cannot_use_lobby`
<br>このタグを持ったプレイヤーは/lobbyコマンドで自身を移動させることができなくなる。

## Item Tags
### `plugin`
#### `locked`: `boolean`
trueの場合、クリエイティブモードのときを除きアイテムはそのスロットから移動できなくなる。

#### `on_right_click`: `compound`
アイテムを手に持って右クリックしたときに実行する処理を登録する。
```
{
    type: string,
    content: string
}
```

#### `on_left_click`: `compound`
アイテムを手に持って左クリックしたときに実行する処理を登録する。
```
{
    type: string,
    content: string
}
```

#### `custom_item_tag`: `string`
プラグイン製のアイテムであることを明示するタグ。
<br>決まった文字列を入力すると、このアイテムを持ってなんらかの動作をした際にプラグインの処理が起動する。

### `weapon`
#### `on_left_click`: `compound`
アイテムを手に持って左クリックしたときに表示するカスタムパーティクルと再生する音を登録する。
<br>代替手段(plugin.on_left_click)があるため削除を検討中
```
{
    particle: string,
    sound: {
        id: string,
        volume?: float,
        pitch?: float
    }
}
```

## Data Pack Functions
### `...systems/plugin/`
#### `api/knockback/specific`
渡された三次元ベクトルを使用して実行者を吹き飛ばす。
```
{
    x: double,
    y: double,
    z: double
}
```

#### `api/knockback/rotation`
渡された力の分だけ実行方向に実行者を吹き飛ばす。
```
{
    strength: double
}
```

## Commands
### /foo
foo!
<br>構文:
```mcfunction
foo
```


### /lobby
プレイヤーをロビーに転送します
<br>構文:
```mcfunction
# 実行者を転送
lobby

# 指定のプレイヤーを転送(OP必須)
lobby <player>
```

### /log
ログファイルを管理します
<br>構文:
```mcfunction
# サーバーのログファイルの内容を全て表示
log server read

# プラグイン固有のログファイルの内容を全て表示
log plugin read

# サーバーのログファイルの指定の行を表示
log server read <int>

# プラグイン固有のログファイルの指定の行を表示
log plugin read <int>

# サーバーのログファイルに書き込み
log server write <string>

# プラグイン固有のログファイルに書き込み
log plugin write <string>

# サーバーのログファイルの内容を削除
log server clear latest

# サーバーの過去のログファイルのアーカイブを全て削除
log server clear archive

# プラグイン固有のログファイルの内容を削除
log plugin clear
```

### /test
いろいろできます
<br>構文:
```mcfunction
# サーバーセレクターの入手
test get_server_selector

# サーバー・プラグイン関連の情報の取得(OP必須)
test get_info <infomation_id>

# 試験的なアイテムの入手用(OP必須)
test get_experimental_item <item_type_id>
```

### /tools
プラグイン製のツールを入手するためのUIを開きます(OP必須)
<br>構文:
```mcfunction
tools
```

## Internal Functions
### Plugin
#### TestPlugin
プラグインのメインクラス。

```java
public static void transfer(Player player, String serverName);

public static void log(String target, String... messages);

public static ItemStack getServerSelector();
  
public static void openServerSelector(Player player);
  
public static boolean runCommand(Entity entity, String command);
  
public static TestPluginEvent.EventRegister events();
  
public static <T extends CommandExecutor & TabCompleter> void setCommandManager(String name, T manager);
```

### Libraries
#### ScoreboardUtils
スコアボードの直感的な操作を可能とする。

```java
public static ScoreboardUtilsObjective getOrCreateObjective(String name);
public static ScoreboardUtilsObjective getOrCreateObjective(String name, Criteria criteria);

public static ScoreboardUtilsObjective createObjective(String name);
public static ScoreboardUtilsObjective createObjective(String name, Criteria criteria);

public static ScoreboardUtilsObjective[] getAllObjectives();

public static void removeObjective(String name);
```

#### ScoreboardUtilsObjective
`ScoreboardUtils`におけるオブジェクトの表現として使用される。
```java
public int getScore(Entity entity);
public int getScore(String name);

public void setScore(Entity entity, int value);
public void setScore(String name, int value);

public void addScore(Entity entity, int value);
public void addScore(String name, int value);

public void resetScore(Entity entity);
public void resetScore(String name);

public String getName();

public void setName();

public DisplaySlot getDisplaySlot();

public void setDisplaySlot(DisplaySlot displaySlot);
```

#### Vector3Builder
三次元ベクトルの容易な計算を可能とする。
- [`...`](src/main/java/com/gmail/subnokoii/testplugin/lib/vector/Vector3Builder.java)

#### RotationBuilder
二次元回転の容易な計算を可能とする。
- [`...`](src/main/java/com/gmail/subnokoii/testplugin/lib/vector/RotationBuilder.java)

#### ItemStackBuilder
アイテムの直感的な作成を可能とする。
- [`...`](src/main/java/com/gmail/subnokoii/testplugin/lib/itemstack/ItemStackBuilder.java)

#### ChestUIBuilder
チェスト型UIの直感的な作成を可能とする。
```java
public ChestUIBuilder(String name, int line);
public ChestUIBuilder(String name, TextDecoration decoration, int line);
public ChestUIBuilder(String name, Color color, int line);
public ChestUIBuilder(String name, TextDecoration decoration, Color color, int line);

public ChestUIBuilder set(int index, UnaryOperator<ChestUIBuilder> builder);

public ChestUIBuilder add(UnaryOperator<ChestUIBuilder> builder);

public void open(Player player);

public Inventory getInventory();

public ChestUIButtonBuilder[] getAllButtons();

public static ChestUIBuilder[] getAll();
```


#### ChestUIButtonBuilder
`ChestUIBuilder`におけるボタンの作成を行う。
- [`...`](src/main/java/com/gmail/subnokoii/testplugin/lib/ui/ChestUIButtonBuilder.java)

#### ChestUIClickEvent
`ChestUIButtonBuilder#onClick()`の引数として渡されるクラス。
```java
public Player getPlayer();

public void playSound(Sound sound, float volume, float pitch);

public void close();

public ChestUIButtonBuilder getClicked();

public boolean runCommand();
```


#### TextFileUtils
テキストファイルの直感的な操作を可能とする。
```java
public static List<String> read(String path);
public static List<String> read(String path, int line);

public static void overwrite(String path, List<String> texts);
public static void overwrite(String path, String text, int line);

public static void write(String path, String text);
public static void write(String path, String text, int line);

public static void erase(String path, int line);
public static void erase(String path);

public static void log(String target, String text);

public static void delete(String path);
```

#### ScheduleUtils
処理に遅延をかけるための関数を集めたユーティリティクラス。
- [`...`](src/main/java/com/gmail/subnokoii/testplugin/lib/other/ScheduleUtils.java)

#### TestPluginEvent
このプラグイン独自のイベントシステムを管理するためのクラス。
- [`...`](src/main/java/com/gmail/subnokoii/testplugin/lib/event/TestPluginEvent.java)

#### EventRegisterer
このプラグイン独自のイベントにイベントリスナーを登録することができる。
```java
public void onLeftClick(Consumer<PlayerClickEvent> listener);

public void onRightClick(Consumer<PlayerClickEvent> listener);

public void onCustomItemUse(Consumer<CustomItemUseEvent> listener);

public void onTick(Consumer<TickEvent> listener);

public void onEnable(Runnable listener);
```

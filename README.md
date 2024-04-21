# TestPlugin
ぷらぐいーん

## Usage

[これ](target/TestPlugin-1.0-SNAPSHOT.jar)をpluginsフォルダにぶち込むと使えるよ

### Scoreboard Objectives
- `plugin.events.player.left_click`
<br>プレイヤーが左クリックを行うと1増加する。</br>

- `plugin.scheduler.tick_listener`
<br>1以上の値をプレイヤーに持たせると、1tick後にプラグインの処理が起動する。</br>

### Entity Tags
- `plugin.permission.cannot_use_lobby`
<br>このタグを持ったプレイヤーは/lobbyコマンドで自身を移動させることができなくなる。</br>

### Item Tags
#### `plugin`
- `locked`: `boolean`
<br>trueの場合、クリエイティブモードのときを除きアイテムはそのスロットから移動できなくなる。</br>

- `on_right_click`: `compound`
<br>アイテムを手に持って右クリックしたときに実行する処理を登録する。</br>
    ```
    {
        type: string,
        content: string
    }
    ```

#### `weapon`
- `on_left_click`: `compound`
<br>アイテムを手に持って左クリックしたときに実行する処理を登録する。</br>
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

### Functions
#### `...systems/plugin/`
- `api/knockback/specific`
<br>渡された三次元ベクトルを使用して実行者を吹き飛ばす。</br>
    ```
    {
        x: double,
        y: double,
        z: double
    }
    ```

- `api/knockback/rotation`
<br>渡された力の分だけ実行方向に実行者を吹き飛ばす。</br>
    ```
    {
        strength: double
    }
    ```

### Commands
#### /foo
構文:
```mcfunction
/foo
```
foo!

#### /ui
構文:
```mcfunction
/ui
```
なんかUIを開きます

#### /lobby
構文:
```mcfunction
# 実行者を転送
/lobby

# 指定のプレイヤーを転送(OP必須)
/lobby <player>
```
プレイヤーをロビーに転送します

### /log
構文:
```mcfunction
# ログファイルの内容を表示
/log read

# ログファイルの指定の行を表示
/log read <int>

# ログファイルに書き込み
/log write <string>

# ログファイルの内容を削除
/log clear
```
ログファイルを管理します。

### Events
#### PlayerJoinEvent
- `Server Selector`をgive

## Internal
### Plugin
#### TestPlugin
プラグインのメインクラス。
- `static void log(String message)`
- `static void transfer(Player player, String serverName)`
- `static TestPlugin get()`

### Libraries
#### ScoreboardUtils
スコアボードの直感的な操作を可能とする。
- `static ScoreboardUtilsObjective getObjective(String name)`
- `static ScoreboardUtilsObjective[] getAllObjectives()`
- `static void removeObjective(String name)`

#### ScoreboardUtilsObjective
`ScoreboardUtils`におけるオブジェクトの表現として使用される。
- `int getScore(Entity entity)`
- `int getScore(String name)`
- `void setScore(Entity entity, int value)`
- `void setScore(String name, int value)`
- `void addScore(Entity entity, int value)`
- `void addScore(String name, int value)`
- `String getName()`
- `DisplaySlot getDisplaySlot()`
- `void setDisplaySlot(DisplaySlot displaySlot)`

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
- `new ChestUIBuilder(String name, int line)`
- `new ChestUIBuilder(String name, TextDecoration decoration, int line)`
- `new ChestUIBuilder(String name, Color color, int line)`
- `new ChestUIBuilder(String name, TextDecoration decoration, Color color, int line)`
- `ChestUIBuilder set(int index, UnaryOperator<ChestUIBuilder> builder)`
- `ChestUIBuilder add(UnaryOperator<ChestUIBuilder> builder)`
- `void open(Player player)`
- `Inventory getInventory()`
- `ChestUIButtonBuilder[] getAllButtons()`
- `static ChestUIBuilder[] getAll()`

#### ChestUIButtonBuilder
`ChestUIBuilder`におけるボタンの作成を行う。
- [`...`](src/main/java/com/gmail/subnokoii/testplugin/lib/ui/ChestUIButtonBuilder.java)

#### ChestUIClickEvent
`ChestUIButtonBuilder#onClick()`の引数として渡されるクラス。
- `Player getPlayer()`
- `void playSound(Sound sound, volume float, pitch float)`
- `void close()`
- `ChestUIButtonBuilder getClicked()`
- `void runCommand()`

#### TextFileUtils
テキストファイルの直感的な操作を可能とする。
- `static List<String> read(String path)`
- `static List<String> read(String path, int line)`
- `static void overwrite(String path, List<String> texts)`
- `static void overwrite(String path, String text, int line)`
- `static void write(String path, String text)`
- `static void write(String path, String text, int line)`
- `static void erase(String path, int line)`
- `static void erase(String path)`
- `static void log(String text)`
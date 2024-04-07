# TestPlugin
ぷらぐいーん

## Usage

### Scoreboard Objectives
- `plugin.events.player.left_click`
<br>プレイヤーが左クリックを行うと1増加する。

- `plugin.scheduler.tick_listener`
<br>1以上の値をプレイヤーに持たせると、1tick後にプラグインの処理が起動する。

### Item Tags
#### `plugin`
- `locked`: `boolean`
<br>trueの場合、クリエイティブモードのときを除きアイテムはそのスロットから移動できなくなる。

- `on_right_click`: `compound`
<br>アイテムを手に持って右クリックしたときに実行する処理を登録する。
    ```
    {
        type: string,
        content: string
    }
    ```

#### `weapon`
- `on_left_click`: `compound`
<br>アイテムを手に持って左クリックしたときに実行する処理を登録する。
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
<br>渡された三次元ベクトルを使用して実行者を吹き飛ばす。
    ```
    {
        x: double,
        y: double,
        z: double
    }
    ```

- `api/knockback/rotation`
<br>渡された力の分だけ実行方向に実行者を吹き飛ばす。
    ```
    {
        strength: double
    }
    ```

### Commands
#### `/foo` -> foo!

### Events
#### PlayerJoinEvent
- `Server Selector`をgive

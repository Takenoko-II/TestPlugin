# TestPlugin
ぷらぐいーん

## Usage

### Scoreboard Objectives
- `plugin.events.player.left_click`
<br>プレイヤーが左クリックを行うと1増加する。</br>

- `plugin.scheduler.tick_listener`
<br>1以上の値をプレイヤーに持たせると、1tick後にプラグインの処理が起動する。</br>

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
#### `/foo` -> foo!
#### `/ui` -> なんかUIを開きます

### Events
#### PlayerJoinEvent
- `Server Selector`をgive

## Internal Libraries
### Classes
#### scoreboard
- `ScoreboardUtils`
<br>スコアボードの直感的な操作を可能とする。</br>
<br></br>

- `ScoreboardUtilsObjective`
<br>`ScoreboardUtils`におけるオブジェクトの表現として使用される。</br>

#### vector
- `Vector3Builder`
<br>三次元ベクトルの容易な計算を可能とする。</br>
<br></br>

- `RotationBuilder`
<br>二次元回転の容易な計算を可能とする。</br>

#### itemstack
- `ItemStackBuilder`
<br>アイテムの直感的な作成を可能とする。</br>

#### ui
- `ChestUIBuilder`
<br>チェスト型UIの直感的な作成を可能とする。</br>
<br></br>

- `ChestUIButtonBuilder`
<br>`ChestUIBuilder`におけるボタンの作成を行う。</br>
<br></br>

- `ChestUIClickEvent`
<br>`ChestUIButtonBuilder#onClick()`の引数として渡されるクラス。</br>
<br></br>

- `ChestUIClickEventListener`
<br>UIの操作を監視する。</br>

#### file
- `TextFileUtils`
<br>テキストファイルの直感的な操作を可能とする。

# TestPlugin
ぷらぐいーん

## Usage

### 1. プラグインとコマンドの通信

プラグインと紐づけられたfunctionを実行することでプラグインの処理を呼び出せます
が、パスがあまりに冗長だったので独立したデータパックを作成してあります

データパックはもうBoAのリポジトリに入れといたはず

`function plugin_api:〇〇`で呼び出せます

コマンドとの通信方法を変更したので、1tick待たずともfunctionを実行してから0tickでプラグインが呼び出されます

要するにコマンドの実行にプラグインは割り込みます

### 2. 使える関数一覧(随時追加)

現在プラグイン用データパックに入ってる関数は以下です

#### plugin_api:knockback/vec3
実行者を三次元ベクトルの方向に飛ばします
<br>引数:
```
{ x: double, y: double, z: double }
```

#### plugin_api:knockback/vec2
実行者を実行方向に飛ばします
<br>引数:
```
{ strength: double }
```

#### plugin_api:server/transfer
実行者をサーバー間移動させます
<br>引数:
```
{ server: string }
```

#### plugin_api:server/logging
ログにメッセージを書き込みます
<br>引数:
```
{ message: string }
```

### 3. プラグイン製カスタムアイテムタグ

右クリック／左クリックの検知はプラグイン用データパック使わなくてもコマンドからできるようにしてあります

アイテムの`components.minecraft:custom_data.PublicBukkitValues`に書き込むことでプラグインが読み取ってくれます

#### on_right_click
```json
{
    "testplugin:on_right_click": {
        "testplugin:type": "run_command",
        "testplugin:content": "コマンド"
    }
}
```
みたいに書くとそのアイテム持って右クリックしたときに`content`に書いたコマンドが実行されます

#### on_left_click
挙動はon_right_clickと同じなので割愛
<br>`on_right_click`の部分を`on_left_click`にすれば左クリックの検知になります

#### locked
```json
{
    "testplugin:locked": true
}
```
みたいに書くとインベントリからアイテムを動かせなくなります(クリエは除く)

#### custom_item_tag
プラグイン内部で使ってるやつなんで割愛

### 4. プラグインによる追加コマンド

前提としてプラグインで追加したコマンドはexecuteとかデータパックから呼び出せないことを念頭に置いておいてください

#### /foo

コマンドがちゃんと追加できてるかを確かめるためだけのコマンドです
実行するとチャットに「foo!」って出るはず

#### /lobby

鯖移動用コンパスなしでロビーサーバーに接続するためのコマンドです
OP持ってるなら/lobbyに続けてプレイヤー名を入力すると指定のプレイヤーをロビーに強制送還できます

タグ「`plugin_api.no_lobby_command`」を持ったプレイヤーは/lobbyコマンドを使えなくなります

#### /test

いろいろできるコマンドです
強いて言うならデバッグ用コマンドです

`/test get_info 〇〇`って入力するといろいろ情報を取得できます(OP必須)
<br>〇〇の部分は補完されるので割愛

`/test get_server_selector`って入力すると鯖移動用コンパス持ってなければもらえます
<br>これはOPいりません

`/test get_experimental_item 〇〇`って入力するとプラグインの力でいろいろ改造されたアイテムがもらえます(OP必須)
<br>そのうちどこかで使うことになりそうなシステムを搭載させてじゃんじゃん追加する予定

#### /tools

プラグインの力で追加したツールを貰うためのチェストUIを開きます(OP必須)
<br>完全自分用ですけど使いたければお好きにどうぞ

#### /log

ログの読み書きやログファイルの削除を行います
<br>主にプラグインのデバッグするときに使うやつなんで割愛

### /database
データベース確認用
<br>割愛

### 5. プラグイン製スコアオブジェクト

随時追加します

#### plugin_api.on_left_click

アイテムを持っている／持っていないに関わらず1回左クリックすると1増えます

### 6. ソースコード・プラグイン導入

プラグイン用データパックは[これ](https://github.com/Kitunegit/BattleofApostolos/tree/main/TestPluginAPI)です
<br>`鯖フォルダ/world/datapacks/`に入れてもらえれば使えます

プラグインのjarファイルは[これ](target/TestPlugin-1.0-SNAPSHOT.jar)です
<br>`鯖フォルダ/plugins/`に入れてもらえれば使えます
<br>頻繁にプッシュしてるので最新のjar欲しくなったらここからどうぞ

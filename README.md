# TestPlugin
ぷらぐいーん

## 主な機能

### Stable
- ディメンション`plugin_api:game_field`, `plugin_api:development`の2種類を追加します
- 追加ディメンションへのアクセスを容易にするアイテム「サーバーセレクター」を全プレイヤーに配布します
- コマンド`/lobby`からオーバーワールドの初期スポーン地点に戻ることができるようになります
- `/function plugin_api:`から始まるコマンドAPIを提供します
- その他デバッグ用コマンドを数個追加します

### Experimental
- 鉄の剣を振ると職業「騎士」のコンボ実装を利用できます
- コマンド`/customitems`からプラグインの管理下に置かれたアイテムを入手できます
- コマンド`/gamefield`からゲームフィールドの地形保存の管理機能を提供します

## ダウンロード・導入

### プラグインAPI(データパック)
1.21.8以降、プラグイン用データパックはプラグイン内部に含まれており、プラグインを起動すると自動で有効化されます
<br>それ以前のバージョンについては[旧リポジトリ](https://github.com/Kitunegit/BattleofApostolos/tree/main/TestPluginAPI)のものを使用してください

### プラグイン.jar

頻繁に更新しています
<br>遷移先ページ中央「view raw」を押すとダウンロードできます
<br>`鯖フォルダ/plugins/`に置いてください

> [!CAUTION]
> このファイルを不特定多数が見える場所に公開するとMinecraftの利用規約に抵触する恐れがあります
> <br>可能な限り直接他者に受け渡すことのないようお願いします

#### 最新版
- [1.21.8～](target/TestPlugin-1.0-SNAPSHOT.jar)

#### 過去バージョン
- [1.21.4](archives/TestPlugin-1.21.4.jar)
- [1.21.3](archives/TestPlugin-1.21.3.jar)
- [1.21.0](archives/TestPlugin-1.21.0.jar)
- [1.20.6](archives/TestPlugin-1.20.6.jar)

## 開発者向け

### プラグイン開発環境
プラグインは別プロジェクト「[TPLCore](https://github.com/Takenoko-II/TPLCore)」に依存しています
<br>直接jarをダウンロードしてプロジェクトのライブラリに置いても問題ありませんが、
<br>ディレクトリ`IdeaProjects/`にこのプロジェクトを置いておくと開発が楽になります
<br>
<br>TestPluginを最新のTPLCoreに同期するには、
1. 最新のTPLCoreを自身のTPLCoreプロジェクトに同期させる
2. TestPluginのターミナルで以下のコマンドを実行してください:
```bash
mvn install:install-file -Dfile="..\TPLCore\build\libs\TPLCore-1.0-SNAPSHOT.jar" -DgroupId="com.gmail.subnokoii78" -DartifactId=TPLCore -Dversion="1.0-SNAPSHOT" -Dpackaging=jar
```

### データパック開発環境
プラグインAPIの補完は受け取れませんが、完全に独立したプロジェクトとしてVSCode等で開発しても問題ありません
<br>[旧データパックリポジトリ](https://github.com/Kitunegit/BattleofApostolos)も必要に応じて適宜利用できます

### リソースパック開発環境
リポジトリは[こちら](https://github.com/Kitunegit/BattleofApostolosRP)

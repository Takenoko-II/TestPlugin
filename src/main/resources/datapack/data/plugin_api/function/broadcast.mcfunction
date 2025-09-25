#> plugin_api:send_message
#
# プラグインにデータを送信し、場合に応じて返り値を受け取ります。
#
# @input
#   args
#       id: string
#   entity 実行者
#   dimension 実行ディメンション
#   location 実行座標
#   rotation 実行方向
#
# @output
#   returns
#
# @api

# 返り値がなかったときのため
    data modify storage plugin_api: out.returnValue set value 0

# ここでctxをストレージにいれてなんたらかんたら: TODO

# プラグインと通信
    function plugin_api:__trigger__ {key: "test_key"}

# 返り値を外部へ返却
    return run data get storage plugin_api: out.returnValue

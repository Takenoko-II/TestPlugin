#> plugin_api:send_message
#
# プラグインにデータを送信し、場合に応じて返り値を受け取ります。
#
# @input
#   args
#       id: string ID
#       args: compound 引数無しの場合は空のコンパウンドを渡す
#
#   entity 実行者
#   dimension 実行ディメンション
#   location 実行座標
#   rotation 実行方向
#
# @output
#   storage
#       plugin_api: broadcasting.out
#   returns
#
# @api

# 返り値がなかったときのため
    data modify storage plugin_api: broadcasting.out set value {return_value: 0}

# プラグインに渡す情報を用意
    tag @s add plugin_api.executor

    summon marker ~ ~ ~ {Tags: ["plugin_api.messenger"]}

    $data modify storage plugin_api: broadcasting.in set value $(args)

    $data modify storage plugin_api: broadcasting.in.id set value "$(id)"

# プラグインと通信
    teleport @e[type=marker,tag=plugin_api.messenger,limit=1] ~ ~ ~ ~ ~

# プラグインが正常に動作しなかった場合に対処するための後処理
    tag @s remove plugin_api.executor

    kill @e[type=marker,tag=plugin_api.messenger,limit=1]

    tag @e[tag=plugin_api.target] remove plugin_api.target

# 返り値を外部へ返却
    return run data get storage plugin_api: broadcasting.out.return_value

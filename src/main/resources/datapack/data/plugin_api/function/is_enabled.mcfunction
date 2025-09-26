#> plugin_api:is_enabled
#
# プラグインAPIが有効化されていれば 1 , そうでなければ 0 を返します。
#
# @output
#   storage
#       plugin_api: broadcasting.out
#   returns
#
# @api

#
    return run function plugin_api:broadcast {id: is_enabled, args: {}}

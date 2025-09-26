#> plugin_api:knockback_vec2
#
# 実行者を実行方向に飛ばします。
#
# @input
#   args
#       strength: double
#   rotation
#
# @output
#   storage
#       plugin_api: broadcasting.out
#   returns
#
# @api

#
    $return run function plugin_api:broadcast {id: knockback_vec2, args: {strength: $(strength)d}}

    return 0

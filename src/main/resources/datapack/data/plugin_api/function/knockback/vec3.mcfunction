#> plugin_api:knockback/vec3
#
# 実行者を実行方向に飛ばします。
#
# @input
#   args
#       vector3: [double, double, double]
#   rotation
#
# @output
#   storage
#       plugin_api: broadcasting.out
#   returns
#
# @api

#
    $return run function plugin_api:broadcast {id: knockback_vec3, args: {vector3: $(vector3)}

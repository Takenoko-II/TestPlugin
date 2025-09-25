#> plugin_api:__trigger__
#
# @input
#   args
#       key: string 実行用のキー
#
# @internal

# マクロを要求するためだけの空のコマンド
    data modify entity 0-0-0-0 _ set string "$(key)"

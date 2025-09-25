#> plugin_api:load
#
# @within tag/function minecraft:load

#
    forceload add 0 0

#
    #> @internal
    # declare storage plugin_api:

    data merge storage plugin_api: {}

    execute if data storage plugin_api: {PluginState: 1} run say TestPlugin API がロードされました

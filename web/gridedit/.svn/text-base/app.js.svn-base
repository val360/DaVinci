/**
 * Created with IntelliJ IDEA.
 * User: val
 * Date: 2/1/13
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
Ext.Loader.setPath('Ext.ux', '../extjs/examples/ux');

Ext.Loader.setConfig({
    enabled: true
});

Ext.require([
    'Ext.data.*', 'Ext.grid.*', 'Ext.ux.CheckColumn'
]);

Ext.application({
    name: 'GRIDEDITING',

    appFolder: 'app',

    controllers: [
        'Customers'
    ],

    launch: function () {
        Ext.create('Ext.container.Viewport', {
            items: [
                {
                    xtype: 'customerList'
                }
            ]
        });
    }
});
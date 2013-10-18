/**
 * Created with IntelliJ IDEA.
 * User: val
 * Date: 2/1/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
Ext.define('davinci.admin.store.Customers', {
    extend: 'Ext.data.Store',
    model: 'GRIDEDITING.model.Customer',
    autoLoad: true,
    pageSize: 20,
    proxy: {
        type: 'ajax',
        url: 'DaVinci/',
        extraParams: {
            store_id: 1
        },
        reader: {
            type: 'json',
            totalProperty: 'totalCount',
            root: 'customers',
            successProperty: 'success'
        }
    },

    listeners: {
        load: function (store) {
            //if need something to do after the store loads
        }
    }

});
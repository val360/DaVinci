Ext.define('AM.store.Users', {
    extend: 'Ext.data.Store',
    model: 'AM.model.User',
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: '../../DaVinci/users.json',
        reader: {
            type: 'json',
            root: 'users',
            successProperty: 'success'
        },
        listeners: {
            'exception': function (proxy, response, operation, eOpts) {
                Ext.Msg.alert('', operation.error.statusText + ': ' + response.responseText);
            }
        }
    }
    /*data:[
        {name:'Ed', email:'ed@sencha.com'},
        {name:'Tommy', email:'tommy@sencha.com'}
    ]*/
});
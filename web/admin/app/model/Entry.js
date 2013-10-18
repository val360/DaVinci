/**
 * Created with IntelliJ IDEA.
 * User: val
 * Date: 2/1/13
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */

var myFields = [];
myFields.push(new Ext.data.Field({
    name: 'entry',
    type: 'string'
}));
myFields.push(new Ext.data.Field({
    name: 'header',
    type: 'string'
}));
myFields.push(new Ext.data.Field({
    name: 'total',
    type: 'string'
}));
myFields.push(new Ext.data.Field({
    name: 'region',
    type: 'string'
}));
myFields.push(new Ext.data.Field({
    name: 'department',
    type: 'string'
}));
for(i = 0; i < 52; i++) {
    var this_field = new Ext.data.Field({
        name: 'week' + (i + 1),
        type: 'string'
    });
    myFields.push(this_field);
}

Ext.define('davinci.admin.model.Entry', {
    extend: 'Ext.data.Model'
    ,fields: myFields
    /*fields: [
        'customerId', 'firstName', 'lastName', 'email', {name: 'active', type: 'bool'}
    ],*/
    ,validations: [
        {type: 'presence', field: 'firstName'},
        {type: 'presence', field: 'lastName'},
        {type: 'presence', field: 'email'}
    ]
});
/**
 * Created with IntelliJ IDEA.
 * User: val
 * Date: 2/1/13
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
Ext.define('GRIDEDITING.model.Customer', {
    extend: 'Ext.data.Model',
    fields: [
        'customerId', 'firstName', 'lastName', 'email', {name: 'active', type: 'bool'}
    ],
    validations: [
        {type: 'presence', field: 'firstName'},
        {type: 'presence', field: 'lastName'},
        {type: 'presence', field: 'email'}
    ]
});
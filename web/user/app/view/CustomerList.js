/**
 * Created with IntelliJ IDEA.
 * User: val
 * Date: 2/1/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
Ext.define('GRIDEDITING.view.CustomerList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.customerList',
    title: 'List of Customers',
    store: 'Customers',
    loadMask: true,
    autoheight: true,
    dockedItems: [
        {
            xtype: 'pagingtoolbar',
            store: 'Customers',
            dock: 'bottom',
            displayInfo: true,
            items: [
                {
                    xtype: 'tbseparator'
                },
                {
                    xtype: 'button',
                    text: 'Add Customer',
                    action: 'add'
                }
            ]
        }
    ],
    plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })
    ],
    selModel: {
        selType: 'cellmodel'
    },
    initComponent: function () {

        this.columns = [
            {
                header: 'Customer Id',
                dataIndex: 'customerId',
                flex: 1
            },{
                header: 'Active',
                dataIndex: 'active',
                flex: 1,
                xtype: 'checkcolumn'
            },
            {
                header: 'First Name',
                dataIndex: 'firstName',
                flex: 1,
                editor: {
                    allowBlank: false
                }
            },
            {
                header: 'Last Name',
                dataIndex: 'lastName',
                flex: 1,
                editor: {
                    allowBlank: false
                }
            },
            {
                header: 'Email Address',
                dataIndex: 'email',
                flex: 1,
                editor: {
                    allowBlank: false,
                    vtype: 'email'
                }
            }
        ];

        this.callParent(arguments);
    }
});
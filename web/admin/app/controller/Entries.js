/**
 * Created with IntelliJ IDEA.
 * User: val
 * Date: 2/1/13
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
Ext.define('davinci.admin.controller.Customers', {
    extend: 'Ext.app.Controller',

    //define the stores
    stores: ['Customers'],
    //define the models
    models: ['Customer'],
    //define the views
    views: ['CustomerList'],

    init: function () {
        this.control({

            'viewport': {
                render: this.onPanelRendered
            },
            'customerList': {
                edit: this.editCustomer
            },
            'checkcolumn': {
                checkchange: this.checkboxChanged
            },
            'customerList button[action=add]': {
                click: this.addCustomer
            }
        });
    },

    onPanelRendered: function () {
        //just a console log to show when the panel is rendered
        console.log('The panel was rendered');
    },

    editCustomer: function (editor, obj) {
        //check if record is dirty
        if(obj.record.dirty) {
            //check if the record is valid
            console.log(obj.record.validate());
            if(obj.record.validate().isValid()) {
                //Make your Ajax request to sync data
                mode = (obj.record.get('customerId') === "") ? 'insert' : 'update';
                this.syncData(obj.rowIdx, mode);
            }
        }
    },

    checkboxChanged: function (column, rowIndex, checked) {
        console.log('Checkbox changed');
        //grid column information
        console.log(column);
        //grid row number
        console.log(rowIndex);
        //the checkbox value
        console.log(checked);
        console.log(this.getCustomersStore().getAt(rowIndex));
        //Make your Ajax request to sync data
        this.syncData(rowIndex, 'update');
    },

    //Sync data with the server
    syncData: function (rowIndex, mode) {
        Ext.Ajax.request({
            url: 'CustomerServlet',
            params: {
                store_id: 1,
                action: mode,
                rowIndex: rowIndex,
                recordInfo: Ext.encode(this.getCustomersStore().getAt(rowIndex).data)
            },
            scope: this,
            //method to call when the request is successful
            success: this.onSaveSuccess,
            //method to call when the request is a failure
            failure: this.onSaveFailure
        });
    },

    onSaveFailure: function (err) {
        //Alert the user about communication error
        Ext.MessageBox.alert('Status', 'Error occured during update');
    },

    onSaveSuccess: function (response, opts) {
        //Remove dirty
        var ajaxResponse = Ext.decode(response.responseText);
        if(ajaxResponse.success) {
            //if we are doing an insert then get the new customerId
            //and update the store record
            if(opts.params.action === 'insert') {
                customerId = ajaxResponse.customerId;
                this.getCustomersStore().getAt(opts.params.rowIndex).set('customerId', customerId);
            }
            this.getCustomersStore().getAt(opts.params.rowIndex).commit();
        } else {
            Ext.MessageBox.alert('Status', 'Error occured during update');
        }
    },

    addCustomer: function (button) {
        var customer = new GRIDEDITING.model.Customer({
            'customerId': '',
            'firstName': '',
            'lastName': '',
            'email': '',
            'active': true
        });

        var panel = button.up('panel');
        editor = panel.editingPlugin;
        editor.cancelEdit();
        this.getCustomersStore().insert(0, customer);
        editor.startEditByPosition({row: 0, column: 2});
    }

});
/**
 * Created with IntelliJ IDEA.
 * User: val
 * Date: 9/27/12
 * Time: 11:32 AM
 */

Ext.require([ 'Ext.data.*', 'Ext.util.*', 'Ext.state.*', 'Ext.form.*' ]);

Ext.onReady(function () {
	Ext.QuickTips.init();

	var loginForm = new Ext.FormPanel({
		labelWidth: 80,
		url: '../../DaVinci/doLogin',
		frame: true,
		title: 'Please Login',
		defaultType: 'textfield',
		defaults: {
			listeners: {
				specialkey: function(field, e) {
					if(e.getKey() == e.ENTER) {
						submit(loginForm);
					}
				}
			}
		},
		monitorValid: true,
		items: [{
			fieldLabel: 'Username',
			name: 'loginUsername',
			allowBlank: false
		}, {
			fieldLabel: 'Password',
			name: 'loginPassword',
			inputType: 'password',
			allowBlank: false
		}],
		buttons: [{
			text: 'Login',
			formBind: true,
			handler: function () {
				submit(loginForm);
			}
		}]
	});

	function submit(loginForm) {
		var form = loginForm.getForm();
		if(form.isValid()) {
			form.submit({
				method: 'POST',
				waitTitle: 'Connecting',
				waitMsg: 'Sending data...',
				success: function (form, action) {
					obj = Ext.JSON.decode(action.response.responseText);
					if(obj.role === 'admin') {
						window.location = '../admin';
					} else if(obj.role === 'user') {
						window.location = '../user';
					}
				},
				failure: function (form, action) {
					if(action.failureType === 'server') {
						obj = Ext.JSON.decode(action.response.responseText);
						Ext.Msg.alert('Login Failed!', obj.errors.reason);
					} else {
						Ext.Msg.alert('Warning!', 'Authentication server is unreachable : ' + action.response.responseText);
					}
					form.reset();
				}
			});
		}
	}

	var loginPanel = Ext.create('Ext.panel.Panel', {
		region: 'center',
		layout: {
			type: 'vbox',
			align: 'center',
			pack: 'center'
		},
		items: loginForm
	});

	//viewport
	Ext.create('Ext.Viewport', {
		padding: 10,
		layout: 'border',
		items: [
			{
				xtype: 'box',
				id: 'header',
				region: 'north',
				html: '<table width=100%><tr><td><h1> DaVinci</h1></td><td align="right" style="padding-right: 10px; padding-top: 2px"><img src="../resources/logo-white.png" height=25/></td></tr></table>',
				height: 30
			},
			loginPanel],
		renderTo:Ext.getBody()
	});
});
//some help function
var DateDiff = {//for correct Date difference
	inDays: function(d1, d2) {
		var t2 = d2.getTime();
		var t1 = d1.getTime();

		return parseInt((t2 - t1) / (24 * 3600 * 1000));
	},

	inWeeks: function(d1, d2) {
		var t2 = d2.getTime();
		var t1 = d1.getTime();

		return parseInt((t2 - t1) / (24 * 3600 * 1000 * 7));
	},

	inMonths: function(d1, d2) {
		var d1Y = d1.getFullYear();
		var d2Y = d2.getFullYear();
		var d1M = d1.getMonth();
		var d2M = d2.getMonth();

		return (d2M + 12 * d2Y) - (d1M + 12 * d1Y);
	},

	inYears: function(d1, d2) {
		return d2.getFullYear() - d1.getFullYear();
	}
};
var MyExt = {};
MyExt.val_cmp = function(v1, v2) {
	if(v1 == undefined || v2 == undefined) return false;
	switch(typeof v1) {
		case "object":
			switch(Object.prototype.toString.call(v1)) {
				case "[object Date]"://for correct Date compare
					return DateDiff.inDays(v1, v2) == 0;
					break;
			}
			break;
		default:
			return v1 == v2;
	}
};
//END some help function

//Multigrouping Plugin
//Store
Ext.define('Ext.data.StoreEx', {
	extend: 'Ext.data.Store',

	constructor: function(config) {
		this.callParent(arguments);

		if(this.total) this.total();
	},

	//the main function. in this function data is grouping and all calculations are done according to the user's defined aggregate functions
	getGroups: function(requestGroupString) {
		var me = this;
		var records = this.data.items,
				length = records.length,
				groups = [],
				pointers = {},
				record,
				groupStr,
				group,
				i;

		if(this.total) this.total_reset();

		var len = 0;
		var fields = [];
		var fns = [];
		var fn_cmp = [];
		var agr = [];
		var collapsed = [];
		var row_cls = [];


		this.groupers.each(function(item) {
			fields.push(item.property);
			fns.push(item.fn);
			fn_cmp.push(item.fn_cmp);
			agr.push(new Array());
			collapsed.push(item.collapsed);
			row_cls.push(item.row_cls);
			len++;
		});

		var arr_val = new Array(len);
		var arr_iter = new Array(len);

		var val, iter, new_per, grp_td;
		iter = groups;
		for(i = 0; i < length; i++) {

			record = records[i];

			if(this.total) this.total_call(record);

			new_per = false;
			grp_td = "";
			for(var j = 0; j < len; j++) {
				val = record.get(fields[j]);
				if(new_per || (fn_cmp[j] ? !fn_cmp[j](val, arr_val[j]) : !MyExt.val_cmp(val, arr_val[j]))) {

					if(arr_iter[j]) {
						arr_iter[j].agrval = fns[j](agr[j]);
						arr_iter[j].collapsed = (Ext.isFunction(collapsed[j]) ? collapsed[j](arr_iter[j].agrval, arr_iter[j].ind) : collapsed[j]);
						agr[j].length = 0;
					}

					iter.push({
						name: val,
						children: [],
						last_grp: (j == len - 1),
						row_cls: row_cls[j],
						ind: iter.length
					});
					arr_iter[j] = iter[iter.length - 1];

					arr_val[j] = val;
					new_per = true;
					iter = iter[iter.length - 1].children;
				} else {
					iter = iter[iter.length - 1].children;
				}
			}
			iter.push(record);
			for(var ii = 0; ii < len; ii++) {
				agr[ii].push(record);
			}

			iter = groups;
		}

		//calculate last groups
		for(var j = 0; j < len; j++) {
			if(arr_iter[j]) {
				arr_iter[j].agrval = fns[j](agr[j]);
				arr_iter[j].collapsed = (Ext.isFunction(collapsed[j]) ? collapsed[j](arr_iter[j].agrval, arr_iter[j].ind) : collapsed[j]);
				agr[j].length = 0;
			}
			iter = iter[iter.length - 1].children;
		}

		if(this.total) arr_iter[0].total = this.total_get();


		return groups;
	}
});
//Grouping feature
Ext.define('Ext.grid.feature.GroupingEx', {
	extend: 'Ext.grid.feature.Grouping',
	alias: 'feature.groupingex',

	//storeGroupers: [],

	eventSelector: '.' + Ext.baseCSSPrefix + 'grid-group-hd1',

	mixins: {
		summary: 'Ext.grid.feature.AbstractSummary'
	},

	getGroupRows: function(group, records, preppedRecords, fullWidth) {
		var me = this,
				children = group.children,
				rows = group.rows = [],
				view = me.view;
		group.viewId = view.id;

		if(group.last_grp) {
			Ext.Array.each(records, function(record, idx) {
				if(Ext.Array.indexOf(children, record) != -1) {
					rows.push(Ext.apply(preppedRecords[idx], {
						depth: 1
					}));
				}
			});
		} else {
			Ext.Array.each(children, function(chl, idx) {
				rows.push(me.getGroupRows(chl, records, preppedRecords, fullWidth));
			});
		}


		delete group.children;


		group.fullWidth = fullWidth;
		if(me.collapsedState[view.id + '-gp-' + group.name] || group.collapsed) {
			group.collapsedCls = me.collapsedCls;
			group.hdCollapsedCls = me.hdCollapsedCls;
		}

		return group;
	},
	getFeatureTpl: function(values, parent, x, xcount) {
		var me = this;

		return [
			'<tpl if="typeof rows !== \'undefined\'">',

			'{[this.printGroupRow(values)]}',

			'<tr id="{viewId}-gp-{name}" class="' + Ext.baseCSSPrefix + 'grid-group-body ' + (me.startCollapsed ? me.collapsedCls : '') + ' {collapsedCls}"><td colspan="' + parent.columns.length + '">{[this.recurse(values)]}</td></tr>',

			'{[(values.agrval ? ( values.agrval.sum ? this.printSummaryRow(values) : "") : "" )]}',

			'{[(values.total ? this.printTotalRow(values.total) : "" )]}',

			'</tpl>'
		].join('');
	},
	//reload metaRowTpl for customizing group row. is called from printGroupRow
	metaRowTpl: [
		'<tr class="{row_cls} ' + Ext.baseCSSPrefix + 'grid-group-hd1 {addlSelector} {[this.embedRowCls()]} {hdCollapsedCls} ' + (this.startCollapsed ? this.hdCollapsedCls : '') + '" {[this.embedRowAttr()]}>',
		//'<tr class="{addlSelector} {[this.embedRowCls()]} {hdCollapsedCls} ' + (this.startCollapsed ? this.hdCollapsedCls : '') + '" {[this.embedRowAttr()]}>',
		'<tpl for="columns">',
		'<td class="{cls} ' + Ext.baseCSSPrefix + 'grid-cell {grp_cls} ' + Ext.baseCSSPrefix + 'grid-cell-{columnId} {{id}-modified} {{id}-tdCls} {[this.firstOrLastCls(xindex, xcount)]}" {{id}-tdAttr} colspan="{grp_spn}">',
		'<div unselectable="on" class="' + Ext.baseCSSPrefix + 'grid-cell-inner ' + Ext.baseCSSPrefix + 'unselectable" style="{{id}-style}; text-align: {align};">',
		'<div class="{grp_title}">{collapsed} {gridGroupValue} </div>',
		'</div>',
		'</td>',
		'</tpl>',
		'</tr>'
	],

	getFragmentTpl: function() {
		var me = this,
				fragments = me.callParent();

		Ext.apply(fragments, me.getGroupFragments());//custom function getGroupFragments
		Ext.apply(fragments, me.getTotalFragments());//custom function getTotalFragments

		Ext.apply(fragments, me.getSummaryFragments());//use standard function getSummaryFragments

		//all calculations are done in the store
		/*if (me.showSummaryRow) {
		 me.summaryGroups = me.view.store.getGroups();
		 me.summaryData = me.generateSummaryData();
		 }*/
		return fragments;
	},
	getGroupFragments: function() {
		var fragments = {};
		if(true) {
			Ext.apply(fragments, {
				printGroupRow: Ext.bind(this.printGroupRow, this)
			});
		}
		return fragments;
	},
	getTotalFragments: function() {
		var fragments = {};
		if(true) {
			Ext.apply(fragments, {
				printTotalRow: Ext.bind(this.printTotalRow, this)
			});
		}
		return fragments;
	},

	//control collapsing and expanding
	_onGroupClick: function() { },
	onGroupClick: function(view, group, idx, foo, e) {
		if(foo.target.className.indexOf('grid-group-title') != -1) {//if click only on group cell (not row)
			//group = group.parentElement;
			var me = this,
					toggleCls = me.toggleCls,
					groupBd = Ext.fly(group.nextSibling, '_grouping');
			if(groupBd.hasCls(me.collapsedCls)) {
				me.expand(groupBd);
			} else {
				me.collapse(groupBd);
			}
		} else this._onGroupClick(view, group, idx, foo, e);
	},


	//print summary
	//printSummaryRow calls getPrintData
	getPrintData: function(values) {
		var me = this,
				columns = me.view.headerCt.getColumnsForTpl(),
				i = 0,
				length = columns.length,
				data = [],
			//name = me.summaryGroups[index - 1].name,
			//active = me.summaryData[name],
				column;

		for(; i < length; ++i) {
			column = columns[i];

			//var active = {};
			if(values.agrval) {
				if(values.agrval.sum) {
					if(values.agrval.sum.hasOwnProperty(column.dataIndex)) {
						//active[column.id] = values.agrval.sum[column.dataIndex];
						column.gridSummaryValue = values.agrval.sum[column.dataIndex];
					}
				}
			}
			//column.gridSummaryValue = this.getColumnValue(column, active);

			data.push(column);
		}
		return data;
	},

	//print total
	printTotalRow: function(total) {
		var inner = this.view.getTableChunker().metaRowTpl.join(''),
				prefix = Ext.baseCSSPrefix;
		inner = inner.replace(prefix + 'grid-row', prefix + 'grid-row-summary grid-row-summary-total');
		inner = inner.replace('{{id}}', '{gridTotalValue}');
		inner = inner.replace(this.nestedIdRe, '{id$1}');
		inner = inner.replace('{[this.embedRowCls()]}', '{rowCls}');
		inner = inner.replace('{[this.embedRowAttr()]}', '{rowAttr}');
		inner = Ext.create('Ext.XTemplate', inner, {
			firstOrLastCls: Ext.view.TableChunker.firstOrLastCls
		});

		return /*this.printBeforeTotal() + */inner.applyTemplate({
			columns: this.getTotalData(total)
		});
	},
	getTotalData: function(total) {
		var me = this,
				columns = me.view.headerCt.getColumnsForTpl(),
				i = 0,
				length = columns.length,
				data = [],
				column;
		for(; i < length; ++i) {
			column = columns[i];

			if(total.hasOwnProperty(column.dataIndex)) {
				//if (total[column.dataIndex].render) column.gridTotalValue = total[column.dataIndex].render;
				//else column.gridTotalValue = total[column.dataIndex].val;
				column.gridTotalValue = total[column.dataIndex];
			}

			data.push(column);
		}
		return data;
	},

	//print group
	printGroupRow: function(values) {

		var inner = this.metaRowTpl.join(''),
				prefix = Ext.baseCSSPrefix;

		inner = inner.replace(prefix + 'grid-row', prefix + 'grid-row-summary');
		//inner = inner.replace('{{id}}', '{gridGroupValue}');
		inner = inner.replace(this.nestedIdRe, '{id$1}');
		inner = inner.replace('{[this.embedRowCls()]}', '{rowCls}');
		inner = inner.replace('{[this.embedRowAttr()]}', '{rowAttr}');
		inner = Ext.create('Ext.XTemplate', inner, {
			firstOrLastCls: Ext.view.TableChunker.firstOrLastCls
		});

		return inner.applyTemplate({
			columns: this.getPrintDataGroup(values),
			row_cls: values.row_cls,
			collapsedCls: values.collapsedCls,
			hdCollapsedCls: values.hdCollapsedCls
		});
	},

	getPrintDataGroup: function(values) {
		var me = this,
				columns = me.view.headerCt.getColumnsForTpl(),
				i = 0,
				length = columns.length,
				data = [],
				column;

		for(; i < length; ++i) {
			column = columns[i];

			//var active = {};
			if(values.agrval) {
				if(values.agrval.grp) {
					if(values.agrval.grp.hasOwnProperty(column.dataIndex)) {
						//active[column.id] = values.agrval.grp[column.dataIndex];

						if(values.agrval.grp[column.dataIndex].render) column.gridGroupValue = values.agrval.grp[column.dataIndex].render; else column.gridGroupValue = values.agrval.grp[column.dataIndex].val;

						if(values.agrval.grp[column.dataIndex].sp) {
							column.grp_spn = values.agrval.grp[column.dataIndex].sp;
							column.grp_cls = 'grp-td';
							column.grp_title = Ext.baseCSSPrefix + 'grid-group-title';
							i += values.agrval.grp[column.dataIndex].sp - 1;
						} else {
							column.grp_spn = 1;
							column.grp_cls = '';
							column.grp_title = '';
						}

					}
				}
			}
			//column.gridSummaryValue = this.getColumnValue(column, active);
			data.push(column);
		}
		return data;
	}

});
//END Multigrouping Plugin

Ext.onReady(function() {

	var Store = new Ext.data.StoreEx({
		groupers: [
			{
				property: 'name',
				collapsed: function(agrval/*object retruned in fn function*/, ind/*index*/) {
					//determine if current group is collapsed
					return ind != 0;
				},
				row_cls: 'border-row-gtr',// some stylesheet class for grouping row
				fn_cmp: function(a, b) {
					//some custom function for comparison
					return a == b;
				},
				fn: function(arr/*array of records in the current group*/) {//this function is called in StoreEx.getGroups for each group
					if(!arr[0]) return;

					var val1 = 0;
					var val2 = 0;

					var header = arr[0].get('name');
					;

					for(var i = 0; i < arr.length; i++) {
						val1 += arr[i].get('val1');
						val2 += arr[i].get('val2');
					}
					/*
					 have to return such object
					 {
					 'sum': {},//if need to show sum row
					 'grp': {}//obligatory
					 }
					 */
					return {
						'sum': { 'name'/*field in the store*/: 'Total'/*value to show in this column*/, "val1": val1 },
						'grp': {
							'name': { 'val': header/*value to show in this column*/, 'sp': 2/*collspan property for current column (default 1)*/ },
							"val2": { 'val': val2 }
						}
					};
				}
			},
			{
				property: 'email',
				collapsed: true,
				fn: function(arr) {
					if(!arr[0]) return;

					var val = arr[0].get('email');

					var val2 = 0;
					var val3 = true;

					for(var i = 0; i < arr.length; i++) {
						val2 += arr[i].get('val2');
						val3 = val3 && arr[i].get('val3');
					}

					return {
						'grp': {
							'email': { 'val': val, 'sp': 1 },
							"val2": { 'val': val2 },
							"val3G": { 'val': val3, 'render': '<div class="grid-checkheader">click on me: ' + val3 + '</div>' }
						}
					};
				}
			}
		],
		total: function() {//if you need Total row for whole
			var sum = 0;

			this.total_reset = function(r) {//is called in the start of StoreEx.getGroups
				sum = 0;
			};

			this.total_call = function(r) {//is called for every record
				sum += r.get('val1');
			};

			this.total_get = function() {//is called in the end to show total row
				return { 'name': 'Total', "val1": sum };
			};
		},
		fields:['name', 'email', 'val1', 'val2', 'val3'],
		//DATA HAS TO BE SORTED BY GROUPING FIELDS
		//in current example - by name, email
		data:[
			{ 'name': 'name1', "email":"em_1_1", "phone":"555-111-1224", "val1": 11, "val2": 10, "val3": true },
			{ 'name': 'name1', "email":"em_1_1", "phone":"555-111-1224", "val1": 10, "val2": 10, "val3": true },
			{ 'name': 'name1', "email":"em_1_2", "phone":"555-111-1224", "val1": 23, "val2": 53, "val3": false },
			{ 'name': 'name2', "email":"em_2_1", "phone":"555-222-1234", "val1": 45, "val2": 67, "val3": false },
			{ 'name': 'name2', "email":"em_2_1", "phone":"555-222-1234", "val1": 56, "val2": 45, "val3": true },
			{ 'name': 'name2', "email":"em_2_2", "phone":"555-222-1244", "val1": 56, "val2": 43, "val3": false },
			{ 'name': 'name2', "email":"em_2_2", "phone":"555-222-1254", "val1": 78, "val2": 23, "val3": true }
		]
	});

	var grid = Ext.create('Ext.grid.Panel', {
		title: 'Multigrouping grid',
		store: Store,
		columns: [
			{ groupable: false, sortable: false,/*disable standard features, also disable in css*/ header: 'Name',  dataIndex: 'name' },
			{ groupable: false, sortable: false, header: 'Email', dataIndex: 'email' },
			{ groupable: false, sortable: false, header: 'Val1', dataIndex: 'val1' },
			{ groupable: false, sortable: false, header: 'Val2', dataIndex: 'val2' },
			{ groupable: false, sortable: false, header: 'Val3', dataIndex: 'val3' },
			{ groupable: false, sortable: false, header: 'Val3G', dataIndex: 'val3G', flex: 1 }//column which is shown only in group row
		],
		height: 400,
		width: 700,
		features: [
			{
				ftype: 'groupingex',
				strTotal: 'TOTAL',
				//storeGroupers: Store.groupers,
				_onGroupClick: function(view, group, idx, foo, e) {
					if(foo.target.className.indexOf('grid-checkheader') != -1) {
						alert("some action");
					}
				}
			}
		],
		renderTo: Ext.getBody()
	});

});
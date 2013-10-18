Ext.define('Assessor.controller.Quiz', {
	extend: 'Ext.app.Controller',
	itemId: 'quizcontroller',
	models: ['Question', 'Choice'],
	stores: ['Question', 'Choice'],
	views: ['QuestionCard'],
// Constants, kinda
	NUM_QUESTIONS: 4,
// Custom Functions
	/**
	 * create store instances
	 */
	createStores: function() {
		if(Ext.getStore('questionstore') == null) {
			var qs = Ext.create('Assessor.store.Question');
			qs.load();
		}
		;
		if(Ext.getStore('choicestore') == null) {
			var cs = Ext.create('Assessor.store.Choice');
			cs.load();
		}
		;
	}, //end createStores
	/**
	 * update buttons
	 */
	updateButtons: function() {
		var index = this.getCardIndex();
		var nb = Ext.ComponentQuery.query('#nextbutton')[0];
		var pb = Ext.ComponentQuery.query('#prevbutton')[0];
		var fb = Ext.ComponentQuery.query('#finishbutton')[0];
		if(index < this.NUM_QUESTIONS) {
			nb.enable();
			fb.disable();
		} else {
			nb.disable();
			fb.enable();
		}
		;
		if(index > 0) {
			pb.enable();
		} else {
			pb.disable();
		}
		;
	}, //end updateButtons
	/**
	 * get active question card index
	 */
	getCardIndex: function() {
		return (Ext.ComponentQuery.query('quizcards')[0].getLayout().activeItem.itemId.split('-')[1]);
	},
	/**
	 * set active question card index
	 */
	setCardIndex: function(index) {
		Ext.ComponentQuery.query('quizcards')[0].getLayout().setActiveItem('questioncard-' + index);
	},
	/**
	 * start the quiz
	 */
	startQuiz: function(args) {
		this.createQuestionCards();
		var sb = Ext.ComponentQuery.query('#startbutton')[0];
		sb.disable();
		this.updateButtons();
	},
	/**
	 * create the UI cards with questions from server.
	 */
	createQuestionCards: function() {
		var qc = Ext.ComponentQuery.query('quizcards')[0];
		for(i = 0; i < this.NUM_QUESTIONS; i++) {
			card = Ext.create('Assessor.view.QuestionCard');
			card.itemId = 'questioncard-' + i.toString();
			qc.add(card);
		}
		;
		this.updateButtons();
	},
	/**
	 * finishQuiz -- finishes and scores the quiz
	 * @param {Object} args
	 */
	finishQuiz: function(args) {
		this.localState.set('quizFinished', true);
	},
	//
	nextQuestion: function(args) {
		console.log('\nnextQuestion');
		var cardlayout = Ext.ComponentQuery.query('quizcards')[0].getLayout();
		var activeIndex = cardlayout.activeItem.itemId.split('-')[1];
		console.log(activeIndex);
		if(activeIndex < this.NUM_QUESTIONS) {
			activeIndex++;
			this.setCardIndex(activeIndex);
		}
		;
		this.updateButtons();
	},
	//
	prevQuestion: function(args) {
		console.log('\nprevQuestion');
		var cardlayout = Ext.ComponentQuery.query('quizcards')[0].getLayout();
		var activeIndex = cardlayout.activeItem.itemId.split('-')[1];
		console.log(activeIndex);
		if(activeIndex > 0) {
			activeIndex--;
			this.setCardIndex(activeIndex);
		}
		;
		this.updateButtons();
	},
	//
	init: function() {
		this.control({
			'#nextbutton': {
				click: this.nextQuestion
			},
			'#prevbutton': {
				click: this.prevQuestion
			},
			'#startbutton': {
				click: this.startQuiz
			},
			'#finishbutton': {
				click: this.finishQuiz
			},
		})
	}
})

Ext.define('Assessor.view.QuizCards', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.quizcards',
	itemId: 'quizcards',
	layout: 'card',
	activeItem: 0,
	items: []
})

Ext.define('Assessor.view.QuestionCard', {
	extend: 'Ext.form.Panel',
	alias: 'widget.questioncard',
	layout: 'anchor',
	items: [
		{
			xtype: 'displayfield',
			itemId: 'questionfield',
			name: 'questionfield',
			fieldLabel: 'Question',
			value: ''
		},
		{
			xtype: 'radiogroup',
			itemId: 'choicegroup',
			columns: 1,
			vertical: true,
			items: [
				{
					boxLabel: '',
					name: 'choice',
					value: 1
				},
				{
					boxLabel: (100 * Math.random()),
					name: 'choice',
					value: 2
				},
				{
					boxLabel: (100 * Math.random()),
					name: 'choice',
					value: 3
				}
			]
		}
	]
})

Ext.define('Assessor.store.Question', {
	extend: 'Ext.data.Store',
	autoLoad: true,
	autoSync: true,
	model: 'Assessor.model.Question',
	storeId: 'questionstore'
})

Ext.define('Assessor.model.Question', {
	extend: 'Ext.data.Model',
	fields: [
		{name: 'id', type: 'int'},
		{name: 'text', type: 'string'},
		{name: 'resource_uri', type: 'string'}
	],
	proxy: {
		type: 'rest',
		url: '/api/v1/question/',
		headers: {
			'accept':'application/json',
			'content-type':'application/json'
		},
		noCache: false,
		reader: {
			type: 'json',
			root: 'objects',
			idAttribute: 'id'
		},
		writer: {
			type: 'json'
		}
	}
})
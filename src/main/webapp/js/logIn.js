function LogInModel(login, password) {
	this.login = login;
	this.password = password;
	this.isLogInSuccess = false;
}

var LOGIN_EVENTS = {
	LOGIN_CHANGE : "LOGIN_CHANGE",
	PASSWORD_CHANGE : "PASSWORD_CHANGE",
	ON_ENTER : "ON_ENTER"
};

function PasswordController(serverURL, refreshCallback, logInCallback) {

	this.serverURL = serverURL;
	this.model = new LogInModel("", "");

	this.handle = function(eventInf) {
		if (eventInf.name === LOGIN_EVENTS.LOGIN_CHANGE) {
			if (checkNotNullAndUndefined(eventInf.data))
				this.model.login = eventInf.data.replace(/([\s])\s*/g, "");
		} else if (eventInf.name === LOGIN_EVENTS.PASSWORD_CHANGE) {
			this.model.password = eventInf.data;
		} else if (eventInf.name === LOGIN_EVENTS.ON_ENTER) {
			this.logIn();
		}
		this.refresh();
	};
	this.checkLoginAndPasswordCorrect = function(login, password) {
		if (!checkNotNullAndUndefined(login)) {
			alert("Необходимо ввести логин");
			return false;
		}
		if (!checkNotNullAndUndefined(password)) {
			alert("Необходимо ввести пароль");
			return false;
		}
		if (login === "") {
			alert("Логин не может быть пустым");
			return false;
		}
		if (password === "") {
			alert("Пароль не может быть пустым");
			return false;
		}
		return true;
	};
	this.logIn = function() {
		var callback = this.logInCallback;
		var model = this.model;
		if (this.checkLoginAndPasswordCorrect(model.login,
   				model.password)) {
			sendRequest(createLogInRequest(model.login,
					model.password), function(xml) {
				if (checkIsResponseOk(xml)) {
					callback();
					model.isLogInSuccess = true;
				} else
					alert("Не удалось войти с такими данными");
			}, this.serverURL, function(xml) {
				alert("Не удалось войти с такими данными");
			});
		}
	};
	this.refresh = refreshCallback;
	this.logInCallback = logInCallback;

}

function LogInDialogView(model, eventHandler) {
	var rootElement = $("<div id=\"logInDialog\" title=\"Введите пароль\"></div>");
	this.rootElement = rootElement;
	rootElement.dialog({
		dialogClass : "no-close",
		autoOpen : false,
		height : 256,
		width : 243,
		modal : true,
		buttons : {
			"Войти" : function() {
				eventHandler(createEvent(LOGIN_EVENTS.ON_ENTER, null));
			}
		}
	});
	rootElement.append($("<label for=\"login\">Логин</label>"));
	var inputLogin = $("<input></input>").attr("type", "text")
			.attr("value", "").attr("id", "login").on(
					"input",
					function() {
						eventHandler(createEvent(LOGIN_EVENTS.LOGIN_CHANGE,
								this.value));
					});
	this.login = inputLogin;
	rootElement.append(inputLogin);
	rootElement.append($("<label for=\"password\">Пароль</label>"));
	var inputPassword = $("<input></input>").attr("type", "password").attr(
			"value", "").attr("id", "password").on("input", function() {
		eventHandler(createEvent(LOGIN_EVENTS.PASSWORD_CHANGE, this.value));
	});
	rootElement.append(inputPassword);
	this.password = inputPassword;
	this.model = model;
	this.refresh = function() {
		this.login.val(this.model.login);
		this.password.val(this.model.password);
	};
	this.openDialog = function() {
		this.rootElement.dialog("open");
	};
	this.closeDialog = function() {
		this.rootElement.dialog("close");
	};
}

var LOGIN = {
	openLogInDialog : function(logInCallback) {
		LOGIN.logInController = new PasswordController(CONSTS.webServiceURL,
				function() {
					LOGIN.logInView.refresh();
				}, function() {
					LOGIN.logInView.closeDialog();
					logInCallback();
				});
		LOGIN.logInView = new LogInDialogView(LOGIN.logInController.model,
				function(eventInf) {
					LOGIN.logInController.handle(eventInf);
				});
		LOGIN.logInView.openDialog();
	},
	logInController : null,
	logInView : null,
	isLogInSuccess : function() {
		return checkNotNullAndUndefined(this.logInController) ? this.logInController.model.isLogInSuccess
				: false;
	},
	getLogIn : function() {
		return this.isLogInSuccess() ? this.logInController.model.login : null;
	},
	getPassword : function() {
		return this.isLogInSuccess() ? this.logInController.model.password
				: null;
	},
	createRequestHeader : function() {
		var password = this.getPassword();
		var logIn = this.getLogIn();

		if (this.isLogInSuccess()) {
			return createHeaderWithLoginAndPass(password, logIn);
		} else
			return "<soapenv:Header/>";
	}

};

function createHeaderWithLoginAndPass(password, logIn) {
	var currentDate = new Date();
	var expireDate = new Date();
	expireDate.setHours(expireDate.getHours() + 1);
	return "<soapenv:Header xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" >"
			+ "<wsse:Security>"
			+ "<wsu:Timestamp wsu:Id=\"Timestamp-7\" xmlns:wsu=\"http://www.docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
			+ "<wsu:Created>"
			+ convertDateToXmlString(currentDate)
			+ "</wsu:Created>"
			+ "<wsu:Expires>"
			+ convertDateToXmlString(expireDate)
			+ "</wsu:Expires>"
			+ "</wsu:Timestamp>"
			+ "<wsse:UsernameToken xmlns:wsu=\"http://www.docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
			+ "<wsse:Username>"
			+ logIn
			+ "</wsse:Username>"
			+ "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">"
			+ password
			+ "</wsse:Password>"
			+ "</wsse:UsernameToken>"
			+ "</wsse:Security>" + "</soapenv:Header>";
}

function createLogInRequest(login, password) {
	return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
			+ createHeaderWithLoginAndPass(password, login)
			+ "<soapenv:Body>"
			+ "<good:TestIsOkRequest/>"
			+ "</soapenv:Body>"
			+ "</soapenv:Envelope>";
}
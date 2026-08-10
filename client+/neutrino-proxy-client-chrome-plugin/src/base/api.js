// 猎么登录接口
System.api.base.login = function(params, successCallback){
	return System.axios(System.Constant.Lieme.API_BASE_URL + '/user/login',
			System.Constant.RequestMethod.POST,
			{},
			null,
			params,
			successCallback
		);
};

// 加载图片
System.api.base.loadImage = function(url, successCallback){
	return System.axios(url,
		System.Constant.RequestMethod.GET,
		{},
		'arraybuffer',
		{
			Accept: 'image/webp,image/apng,image/*,*/*;q=0.8'
		},
		successCallback
	);
};

// 解析猎聘简历
System.api.liepin.resumePaser = function(params, successCallback){
	return System.axios(System.Constant.Lieme.API_BASE_URL + '/paser/html',
			System.Constant.RequestMethod.POST,
			{},
			null,
			params,
			successCallback
		);
};

// 更新猎聘简历
System.api.liepin.resumeUpdate = function(params, successCallback){
	return System.axios(System.Constant.Lieme.API_BASE_URL + '/paser/update',
			System.Constant.RequestMethod.POST,
			{},
			null,
			params,
			successCallback
		);
};

// 导入猎聘简历
System.api.liepin.resumeImport = function(params, successCallback){
	return System.axios(System.Constant.Lieme.API_BASE_URL + '/paser/import',
			System.Constant.RequestMethod.POST,
			{},
			null,
			params,
			successCallback
		);
};
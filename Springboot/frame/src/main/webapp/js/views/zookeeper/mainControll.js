$(function() {
	mainControll.init();
})

var mainControll = {
	
}

mainControll.init = function() {
	mainControll.createEvent();
}

mainControll.createEvent = function() {
	
	// Select Znode Value Btn 클릭
	$('#selectZnodeValueBtn').click(function() {
		mainControll.selectZnodeValue();
	})

	// Select Znode State Btn 클릭
	$('#selectZnodeStateBtn').click(function() {
		mainControll.selectZnodeState();
	})

	// Select Znode Child Btn 클릭
	$('#selectZnodeChildBtn').click(function() {
		mainControll.selectZnodeChild();
	})
	
	// Create Btn 클릭
	$('#createZnodeBtn').click(function() {
		mainControll.createZnode();
	})
	
	// Update Btn 클릭
	$('#updateZnodeBtn').click(function() {
		mainControll.updateZnode();
	})
	
	// Delete Btn 클릭
	$('#deleteZnodeBtn').click(function() {
		mainControll.deleteZnoe();
	})
}

// Znode Value 조회
mainControll.selectZnodeValue = function() {
	$('#stateForResult').text('')
	var znodeKey = $('#keyForSelect').val();
	var sendData = {
			znodeKey : znodeKey
	}
	$.ajax({
		'type'			: 'POST',
		'url'			: '/zookeeper/selectValue',
		'data'			: JSON.stringify(sendData),
		'contentType'	: 'application/json; charset=utf-8',
		'dataType'		: 'json', 
		'success'		: function(receiveData) {
			console.log(receiveData);
			var receiveZnodeValue 	= receiveData.znodeValue
			$('#keyForResult').val(znodeKey);
			$('#valueForResult').val(receiveZnodeValue); 
		},
		'error'		: function(error) {
			console.log(error)
		}
	})
}

// Znode State 조회
mainControll.selectZnodeState = function() {
	$('#stateForResult').text('')
	var znodeKey = $('#keyForSelect').val();
	var sendData = {
		znodeKey : znodeKey
	}
	$.ajax({
		'type'			: 'POST',
		'url'			: '/zookeeper/selectState',
		'data'			: JSON.stringify(sendData),
		'contentType'	: 'application/json; charset=utf-8',
		'dataType'		: 'json',
		'success'		: function(receiveData) {
			if (receiveData.result == true) {
				$('#stateForResult').text(receiveData.znodeState);
			} else {
				$('#stateForResult').text('select fail');
			}
		},
		'error'		: function(error) {
			console.log(error)
		}
	})
}

// Znode Child 조회
mainControll.selectZnodeChild = function() {
	$('#stateForResult').text('')
	var znodeKey = $('#keyForSelect').val();
	var sendData = {
		znodeKey : znodeKey
	}
	$.ajax({
		'type'			: 'POST',
		'url'			: '/zookeeper/selectChild',
		'data'			: JSON.stringify(sendData),
		'contentType'	: 'application/json; charset=utf-8',
		'dataType'		: 'json',
		'success'		: function(receiveData) {
			if (receiveData.result == true) {
				console.log(receiveData)
				$('#stateForResult').text(receiveData.znodeChild+ "   length : "+receiveData.znodeChild.length);
			} else {
				$('#stateForResult').text('select fail');
			}
		},
		'error'		: function(error) {
			console.log(error)
		}
	})
}

// Znode 등록
mainControll.createZnode = function() {
	var sendData = {
			znodeKey 	: $('#keyForCreate').val(),
			znodeValue	: $('#valueForCreate').val(),
			znodeType	: $('#typeForCreate').val()
	}
	console.log(sendData);
	$.ajax({
		'type'			: 'POST',
		'url'			: '/zookeeper/create',
		'data'			: JSON.stringify(sendData),
		'contentType'	: 'application/json; charset=utf-8',
		'dataType'		: 'json',
		'success'		: function(receiveData) {
			if (receiveData.result == true) {
				$('#stateForResult').text('create Success');
			} else {
				$('#stateForResult').text('create fail');
			}
		},
		'error'			: function(error) {
			console.log(error);
		}	
	})
}

// Znode 수정
mainControll.updateZnode = function() {
	var sendData = {
			znodeKey	: $('#keyForUpdate').val(),
			znodeValue	: $('#valueForUpdate').val()
	}
	
	$.ajax({
		'type'			: 'POST',
		'url'			: '/zookeeper/update',
		'data'			: JSON.stringify(sendData),
		'contentType'	: 'application/json; charset=utf-8',
		'dataType'		: 'json',
		'success'		: function(receiveData) {
			if (receiveData.result == true) {
				$('#stateForResult').text('update Success');
			} else {
				$('#stateForResult').text('update fail');
			}
		},
		'error'			: function(error) {
			console.log(error);
		}	
	})
}

// Znode 삭제
mainControll.deleteZnoe = function() {
	var sendData = {
			znodeKey : $('#keyForDelete').val()
	}
	
	$.ajax({
		'type'			: 'POST',
		'url'			: '/zookeeper/delete',
		'data'			: JSON.stringify(sendData),
		'contentType'	: 'application/json; charset=utf-8',
		'dataType'		: 'json',
		'success'		: function(receiveData) {
			if (receiveData.result == true) {
				$('#stateForResult').text('delete Success');
			} else {
				$('#stateForResult').text('delete fail');
			}
		},
		'error'			: function(error) {
			console.log(error);
		}	
	})
}

// Znode 상태 조회
mainControll.deleteZnoe = function() {
	var sendData = {
		znodeKey : $('#keyForDelete').val()
	}

	$.ajax({
		'type'			: 'POST',
		'url'			: '/zookeeper/delete',
		'data'			: JSON.stringify(sendData),
		'contentType'	: 'application/json; charset=utf-8',
		'dataType'		: 'json',
		'success'		: function(receiveData) {
			if (receiveData.result == true) {
				$('#stateForResult').text('delete Success');
			} else {
				$('#stateForResult').text('delete fail');
			}
		},
		'error'			: function(error) {
			console.log(error);
		}
	})
}
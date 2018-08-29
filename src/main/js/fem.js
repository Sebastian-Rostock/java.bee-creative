function FEMValue(Data) {
	this._data = Data;
}

(function() {

	function dataIs(Data, Class) {
		return Data && Data.constructor == Class;
	}
	function dataIsString(Data, Start) {
		return dataIs(Data, String) && Data.slice(0, 1) == Start;
	}
	function dataIsArray(Data, Length) {
		return dataIs(Data, Array) && Data.length == Length;
	}

	FEMValue.VOID = new FEMValue('V');
	FEMValue.TRUE = new FEMValue('T');
	FEMValue.FALSE = new FEMValue('F');

	FEMValue.from = function(Source) {
		if (Source == null || Source == undefined) return FEMValue.VOID;
		var Class = Data.constructor
		if (Class == Array) return FEMValue.fromArray(Source);
		if (Class == Number) return FEMValue.fromNumber(Source);
		if (Class == String) return FEMValue.fromString(Source);
		if (Class == Boolean) return FEMValue.fromBoolean(Source);
		if (Class == Uint8Array) return FEMValue.fromBinary(Source);
		if (Class == FEMValue) return Source;
		return FEMValue.fromObject(Source);
	};
	FEMValue.fromArray = function(Source) {
		var items = [], length = Source.length;
		for (var i = 0; i < length; i++) {
			items.push(FEMValue.from(Source[i]));
		}
		return new FEMValue([ items ]);
	};
	FEMValue.fromObject = function(Source) {
		var keys = [], values = [], key;
		for (key in Source) {
			keys.push(FEMValue.fromString(key));
			values.push(FEMValue.from(Source[key]));
		}
		return new FEMValue([ keys, values ]);
	};
	FEMValue.fromBinary = function(Source) {
		// TODO Uint8Array
		return FEMValue.VOID;
	};
	FEMValue.fromString = function(Source) {
		return new FEMValue('S' + String(Source));
	};
	FEMValue.fromNumber = function(Source) {
		return Source == parseInt(Source) ? FEMValue.fromInteger(Source) : FEMValue.fromDecimal(Source);
	};
	FEMValue.fromDecimal = function(Source) {
		return new FEMValue('I' + parseInt(Source));
	};
	FEMValue.fromInteger = function(Source) {
		return new FEMValue('D' + parseFloat(Source));
	};
	FEMValue.fromBoolean = function(Source) {
		return Source ? FEMValue.TRUE : FEMValue.FALSE;
	};
	FEMValue.fromDatetime = function(Source) {
		// TODO ? Date
		return FEMValue.VOID;
	};
	FEMValue.fromDuration = function(Source) {
		// TODO ?
		return FEMValue.VOID;
	};

	FEMValue.prototype.isVoid = function() {
		return this._data === 'V';
	};
	FEMValue.prototype.isTrue = function() {
		return this._data === 'T';
	};
	FEMValue.prototype.isFalse = function() {
		return this._data === 'F';
	};
	FEMValue.prototype.isArray = function() {
		return dataIsArray(this._data, 1);
	};
	FEMValue.prototype.isObject = function() {
		return dataIsArray(this._data, 2);
	};
	FEMValue.prototype.isBinary = function() {
		return dataIsString(this._data, 'B');
	};
	FEMValue.prototype.isString = function() {
		return dataIsString(this._data, 'S');
	};
	FEMValue.prototype.isDecimal = function() {
		return dataIsString(this._data, 'D');
	};
	FEMValue.prototype.isInteger = function() {
		return dataIsString(this._data, 'I');
	};
	FEMValue.prototype.isBoolean = function() {
		return this.isTrue() || this.isFalse();
	};
	FEMValue.prototype.isDatetime = function() {
		return dataIsString(this._data, 'W');
	};
	FEMValue.prototype.isDuration = function() {
		return dataIsString(this._data, 'H');
	};

	FEMValue.prototype.asArray = function() {
		if (!this.isArray()) return undefined;
		var result = [], items = this._data[0], length = items.length;
		for (var i = 0; i < length; i++) {
			result.push(items[i].asNative());
		}
		return result;
	};
	FEMValue.prototype.asObject = function() {
		if (!this.isArray()) return undefined;
		var result = {}, keys = this._data[0], values = this._data[1], length = keys.length;
		for (var i = 0; i < length; i++) {
			result[keys[i].asString()] = values[i].asNative();
		}
		return result;
	};
	FEMValue.prototype.asBinary = function() {
		// TODO
		return undefined;
	};
	FEMValue.prototype.asString = function() {
		// TODO
		return undefined;
	};
	FEMValue.prototype.asNumber = function() {
		// TODO
		return undefined;
	};
	FEMValue.prototype.asBoolean = function() {
		if (this.isTrue()) return true;
		if (this.isFalse()) return false;
		return undefined;
	};
	FEMValue.prototype.asDatetime = function() {
		// TODO
		return undefined;
	};
	FEMValue.prototype.asDuration = function() {
		// TODO
		return undefined;
	};

})()

alert(typeof 'as')
alert(new String('as').constructor === String)

alert(new FEMValue().isBinary)
//
//transport
//
//V void undefined
//T true Boolean
//F false Boolean
//B Binary [bytes...]
//S String 
//D Decimal Number
//I Integer Number
//W Datetime [days, millis]
//H Duration [months, millis]
//[] Array 
//
//
//json
//
//X[] -> X[][1]
//
//{X:Y} -> [X[], Y[]]
//
//

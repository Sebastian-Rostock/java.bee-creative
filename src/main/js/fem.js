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
		if (Source == undefined) return undefined;
		if (Source == null) return FEMValue.VOID;
		var Class = Source.constructor;
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
			items.push(FEMValue.from(Source[i])._data);
		}
		return new FEMValue([ items ]);
	};
	FEMValue.fromObject = function(Source) {
		var keys = [], values = [], key;
		for (key in Source) {
			keys.push(FEMValue.fromString(key)._data);
			values.push(FEMValue.from(Source[key])._data);
		}
		return new FEMValue([ keys, values ]);
	};
	FEMValue.fromBinary = function(Source) {
		// TODO Uint8Array
		return FEMValue.VOID;
	};
	FEMValue.fromNumber = function(Source) {
		return parseInt(Source) == Source ? FEMValue.fromInteger(Source) : FEMValue.fromDecimal(Source);
	};
	FEMValue.fromString = function(Source) {
		return new FEMValue('S' + String(Source));
	};
	FEMValue.fromDecimal = function(Source) {
		return new FEMValue('D' + parseFloat(Source));
	};
	FEMValue.fromInteger = function(Source) {
		return new FEMValue('I' + parseInt(Source));
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

	FEMValue.prototype.data = function() {
		return this._data;
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
			result.push(new FEMValue(items[i]).asNative());
		}
		return result;
	};
	FEMValue.prototype.asObject = function() {
		if (!this.isObject()) return undefined;
		var result = {}, keys = this._data[0], values = this._data[1], length = keys.length;
		for (var i = 0; i < length; i++) {
			result[new FEMValue(keys[i]).asString()] = new FEMValue(values[i]).asNative();
		}
		return result;
	};
	FEMValue.prototype.asBinary = function() {
		// TODO
		return undefined;
	};
	FEMValue.prototype.asNumber = function() {
		if (this.isDecimal()) return this.asDecimal();
		if (this.isInteger()) return this.asInteger();
		return undefined;
	};
	FEMValue.prototype.asString = function() {
		if (this.isString()) return this._data.slice(1);
		return undefined;
	};
	FEMValue.prototype.asDecimal = function() {
		if (this.isDecimal()) return parseFloat(this._data.slice(1));
		return undefined;
	};
	FEMValue.prototype.asInteger = function() {
		if (this.isInteger()) return parseInt(this._data.slice(1));
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
	FEMValue.prototype.asNative = function() {
		if (this.isVoid()) return null;
		if (this.isTrue()) return true;
		if (this.isFalse()) return false;
		if (this.isArray()) return this.asArray();
		if (this.isObject()) return this.asObject();
		if (this.isString()) return this.asString();
		if (this.isBinary()) return this.asBinary();
		if (this.isDecimal()) return this.asDecimal();
		if (this.isInteger()) return this.asInteger();
		if (this.isDatetime()) return this.asDatetime();
		if (this.isDuration()) return this.asDuration();
		return undefined;
	};
	FEMValue.prototype.toString = function() {
		return JSON.stringify(this._data);
	};

})();

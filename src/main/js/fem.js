

function FEMValue(Data) {
	this.data = Data;
}
function(){
	
FEMValue.prototype.isVoid = function() {
	return data === 'V'
};

FEMValue.prototype.isTrue = function() {
	return data === 'T'
};

FEMValue.prototype.isFalse= function() {
	return data === 'F'
};


FEMValue.prototype.isBinary= function() {
	return data === 'F'
};

}();


alert(new FEMValue().isBinary)

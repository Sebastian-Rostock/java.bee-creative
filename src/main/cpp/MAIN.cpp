#include "MAIN.hpp"
#include <iostream>

using namespace bee_creative;

int main() {

	INT8S d = 200;
	INT16S e = 60000;

	cout << d.asINT8 << '\n';
	cout << d.asUINT8 << '\n';
	cout << e.asINT16 << '\n';
	cout << e.asUINT16 << '\n';
//	cout << sizeof (IAMIndex::OBJECT) << '\n';
//	cout << sizeof (IAMListing::OBJECT) << '\n';
//	cout << sizeof (IAMMapping::OBJECT) << '\n';

	return 0;
}


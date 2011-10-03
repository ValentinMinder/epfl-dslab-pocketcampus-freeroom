namespace java org.pocketcampus.plugin.test.shared

/**
* Service definition for the test plugin.
*/

include "../include/common.thrift"

service TestService {
	i32 getBar();
}
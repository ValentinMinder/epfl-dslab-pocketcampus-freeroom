#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
	if(argc != 4) {
		printf("Invocation error: 3 args required");
		return 0;
	}
	char cmd[512];
	setreuid(geteuid(), getuid());
	sprintf(cmd, "java -jar %s %s > %s  2>&1 & echo Server successfully started with pid $!", argv[1], argv[2], argv[3]);
	system(cmd);
	return 0;

	printf("uid=%d euid=%d\n", getuid(), geteuid());
	system( "./sc.sh" );
	//setuid( 502 );
	setreuid(geteuid(), getuid());
	printf("uid=%d euid=%d\n", getuid(), geteuid());
	system( "./sc.sh" );

	return 0;
}

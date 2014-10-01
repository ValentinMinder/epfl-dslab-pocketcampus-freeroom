#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
	if(argc != 2) {
		printf("Invocation error: team not specified\n");
		return 0;
	}
	char cmd[512];
	setreuid(geteuid(), getuid());
	sprintf(cmd, "ps aux | grep pocketcampus-server-%s | grep -v grep", argv[1]);
	system("whoami");
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

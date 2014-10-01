#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
	if(argc != 2) {
		printf("Invocation error: 1 arg required");
		return 0;
	}
	//char cmd[128];
	setreuid(geteuid(), getuid());
	//sprintf(cmd, "ps aux | grep pocketcampus-server-%s | grep -v grep | tr '\t' ' ' | tr -s ' ' | cut -f 2 -d ' ' | xargs kill -%s", argv[1], argv[2]);
	system(argv[1]);
	return 0;

	printf("uid=%d euid=%d\n", getuid(), geteuid());
	system( "./sc.sh" );
	//setuid( 502 );
	setreuid(geteuid(), getuid());
	printf("uid=%d euid=%d\n", getuid(), geteuid());
	system( "./sc.sh" );

	return 0;
}

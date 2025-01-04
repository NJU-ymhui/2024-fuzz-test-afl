#include <stdio.h>
#include <stdlib.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <string.h>

#define SHM_SIZE 65536 // 64KB 共享内存大小

int main(int argc, char *argv[]) {
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <shm_id>\n", argv[0]);
        return 1;
    }

    // 获取共享内存 ID
    int shm_id = atoi(argv[1]);
    if (shm_id <= 0) {
        fprintf(stderr, "Invalid shm_id: %s\n", argv[1]);
        return 1;
    }

    // 挂载共享内存
    char *shared_memory = (char *)shmat(shm_id, NULL, 0);
    if (shared_memory == (char *)-1) {
        perror("Failed to attach shared memory");
        return 1;
    }

    // 读取并打印共享内存内容
    printf("Content of shared memory ID %d:\n", shm_id);
    printf("%s\n", shared_memory);

    // 卸载共享内存
    if (shmdt(shared_memory) == -1) {
        perror("Failed to detach shared memory");
        return 1;
    }

    return 0;
}

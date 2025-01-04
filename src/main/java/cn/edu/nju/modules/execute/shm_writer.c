#include <stdio.h>
#include <stdlib.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <string.h>
#include <unistd.h>

#define SHM_SIZE 65536 // 64KB 共享内存大小

int main(int argc, char *argv[]) {
    if (argc < 3) {
        fprintf(stderr, "Usage: %s <program_path> <input_file_path>\n", argv[0]);
        return 1;
    }

    // 从命令行参数获取插桩程序路径和输入文件路径
    const char *program_path = argv[1];
    const char *input_file_path = argv[2];

    // 创建共享内存
    int shm_id = shmget(IPC_PRIVATE, SHM_SIZE, IPC_CREAT | 0600);
    if (shm_id == -1) {
        perror("Failed to create shared memory");
        return 1;
    }
    printf("Created shared memory ID: %d\n", shm_id);

    // 挂载共享内存
    char *shared_memory = (char *)shmat(shm_id, NULL, 0);
    if (shared_memory == (char *)-1) {
        perror("Failed to attach shared memory");
        return 1;
    }

    // 初始化共享内存
    memset(shared_memory, 0, SHM_SIZE);

    // 构建插桩程序命令
    char command[1024];
    snprintf(command, sizeof(command), "%s %s", program_path, input_file_path);

    // 执行插桩程序并捕获输出
    FILE *fp = popen(command, "r");
    if (fp == NULL) {
        perror("Failed to execute program");
        return 1;
    }

    // 将插桩程序的输出写入共享内存
    size_t offset = 0;
    while (fgets(shared_memory + offset, SHM_SIZE - offset, fp) != NULL) {
        offset += strlen(shared_memory + offset);
        if (offset >= SHM_SIZE) {
            fprintf(stderr, "Shared memory full, truncating output\n");
            break;
        }
    }

    // 关闭插桩程序进程
    pclose(fp);

    printf("Program output written to shared memory.\n");
    printf("Keep this shared memory ID for the reader: %d\n", shm_id);

    // 等待用户输入，保持共享内存段存在
    printf("Press Enter to detach and release shared memory...");
    getchar();

    // 卸载并释放共享内存
    if (shmdt(shared_memory) == -1) {
        perror("Failed to detach shared memory");
        return 1;
    }

    if (shmctl(shm_id, IPC_RMID, NULL) == -1) {
        perror("Failed to remove shared memory");
        return 1;
    }

    printf("Shared memory released.\n");
    return 0;
}

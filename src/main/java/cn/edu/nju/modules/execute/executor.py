import os
import sys
import subprocess
import sysv_ipc

# AFL 默认的共享内存大小和环境变量名
SHM_SIZE = 65536 * 8  # 位总数（64 KB * 8 bits）
SHM_ENV_VAR = "__AFL_SHM_ID"

def calculate_bit_coverage(data):
    """
    计算覆盖率：按位统计 0 和 1 的个数。
    """
    total_bits = len(data) * 8  # 每字节 8 位
    total_zeros = 0
    total_ones = 0

    for byte in data:
        for i in range(8):  # 遍历每位
            if (byte >> i) & 1:
                total_ones += 1
            else:
                total_zeros += 1

    return total_bits, total_zeros, total_ones

def main():
    if len(sys.argv) < 3:
        sys.exit("Usage: python run_and_read.py <program_path> <input_file_path> [additional_args...]")

    program_path = sys.argv[1]
    input_file_path = sys.argv[2]
    additional_args = sys.argv[3:]

    # 创建共享内存
    shm = sysv_ipc.SharedMemory(None, flags=sysv_ipc.IPC_CREX, mode=0o600, size=SHM_SIZE // 8)
    os.environ[SHM_ENV_VAR] = str(shm.id)

    # 构建插桩程序的命令
    command = [program_path, input_file_path] + additional_args

    # 执行插桩程序
    try:
        result = subprocess.run(command, stdout=subprocess.DEVNULL, stderr=subprocess.PIPE, env=os.environ)
        if result.returncode != 0:
            sys.exit(f"Program exited with non-zero status: {result.returncode}")
    except Exception as e:
        sys.exit(f"Failed to execute program: {e}")

    # 读取共享内存内容
    data = shm.read(SHM_SIZE // 8)

    # 计算覆盖率
    total_bits, total_zeros, total_ones = calculate_bit_coverage(data)
    coverage_percentage = (total_ones / total_bits) * 100

    # 输出覆盖率
    print(f"{coverage_percentage:.2f}%")

    # 释放共享内存
    shm.remove()

if __name__ == "__main__":
    main()

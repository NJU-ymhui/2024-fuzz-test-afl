import os
import sys
import subprocess
import sysv_ipc

# AFL 默认的共享内存大小和环境变量名
SHM_SIZE = 65536  # AFL 的共享内存是 64KB（65536 字节）
SHM_ENV_VAR = "__AFL_SHM_ID"


def calculate_coverage(data):
    """
    计算覆盖率：统计共享内存中非零字节数量。
    """
    total_bytes = len(data)  # 位图总大小（64KB）
    non_zero_count = sum(1 for byte in data if byte != 0)  # 非零字节的数量

    coverage_percentage = (non_zero_count / total_bytes) * 100  # 覆盖率百分比
    return total_bytes, non_zero_count, coverage_percentage


def main():
    if len(sys.argv) < 3:
        sys.exit("Usage: python run_and_read.py <program_path> <input_file_path> [additional_args...]")

    program_path = sys.argv[1]
    input_file_path = sys.argv[2]
    additional_args = sys.argv[3:]

    # 创建共享内存
    shm = sysv_ipc.SharedMemory(None, flags=sysv_ipc.IPC_CREX, mode=0o600, size=SHM_SIZE)
    os.environ[SHM_ENV_VAR] = str(shm.id)

    # 初始化共享内存为全 0
    initial_data = bytes([0] * SHM_SIZE)
    shm.write(initial_data)

    print(f"Created shared memory with ID: {shm.id}")

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
    data = shm.read(SHM_SIZE)

    # 计算覆盖率
    total_bytes, non_zero_count, coverage_percentage = calculate_coverage(data)

    # 输出覆盖率
    print(f"Total Bytes in Bitmap: {total_bytes}")
    print(f"Non-Zero Bytes (Covered Branches): {non_zero_count}")
    print(f"{coverage_percentage:.2f}%")

    # 显示共享内存前 100 个字节的内容（十进制）
    decimal_data = [byte for byte in data[:100]]
    print("Shared Memory Content (Decimal, First 100 Bytes):", decimal_data)

    # 释放共享内存
    shm.remove()


if __name__ == "__main__":
    main()

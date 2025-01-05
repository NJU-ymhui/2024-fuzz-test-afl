import os
import sys
import subprocess
import sysv_ipc

# AFL 默认的共享内存大小和环境变量名
SHM_SIZE = 65536  # 位图大小为 64KB（65536 个字节）
SHM_ENV_VAR = "__AFL_SHM_ID"

def calculate_coverage(data):
    """
    计算覆盖率：统计非零槽数量。
    """
    total_slots = len(data)  # 总槽数量（65536）
    non_zero_slots = sum(1 for byte in data if byte != 0)  # 非零槽数量

    # 计算覆盖率
    coverage_percentage = (non_zero_slots / total_slots) * 100

    return total_slots, non_zero_slots, coverage_percentage

def main():
    if len(sys.argv) < 3:
        sys.exit("Usage: python run_and_read.py <program_path> <input_file_path> [additional_args...]")

    program_path = sys.argv[1]
    input_file_path = sys.argv[2]
    additional_args = sys.argv[3:]

    try:
        # 创建共享内存
        shm = sysv_ipc.SharedMemory(None, flags=sysv_ipc.IPC_CREX, mode=0o600, size=SHM_SIZE)
        os.environ[SHM_ENV_VAR] = str(shm.id)
        initial_data = bytes([0] * SHM_SIZE)  # 64KB 的全 0 数据
        shm.write(initial_data)

        print(f"Shared Memory ID: {shm.id}")

        # 构建插桩程序的命令
        command = [program_path, input_file_path] + additional_args

        # 执行插桩程序
        result = subprocess.run(
            command,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.PIPE,
            env=os.environ,
            timeout=10  # 超时时间（秒）
        )
        if result.returncode != 0:
            sys.exit(f"Program exited with non-zero status: {result.returncode}")

        # 读取共享内存内容
        data = shm.read(SHM_SIZE)
        print("Shared Memory Content (Decimal):", list(data[:100]))

        # 计算覆盖率
        total_slots, non_zero_slots, coverage_percentage = calculate_coverage(data)

        # 输出覆盖率信息
        print(f"Total Slots: {total_slots}")
        print(f"Non-Zero Slots: {non_zero_slots}")
        print(f"{coverage_percentage:.2f}%")

    except subprocess.TimeoutExpired:
        sys.exit("Program timed out.")
    except Exception as e:
        sys.exit(f"An error occurred: {e}")
    finally:
        # 释放共享内存
        try:
            shm.remove()
            print("Shared memory removed successfully.")
        except Exception as e:
            print(f"Failed to remove shared memory: {e}")

if __name__ == "__main__":
    main()

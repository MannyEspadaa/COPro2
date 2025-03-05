import java.util.*;

// Class representing a memory partition
class Partition {
    int size; // Size of the partition
    boolean occupied; // Whether the partition is occupied
    int jobId; // ID of the job allocated to this partition

    Partition(int size) {
        this.size = size;
        this.occupied = false; // Initially, the partition is empty
        this.jobId = -1; // No job assigned initially
    }
}

// Class representing a job
class Job {
    int id; // Job ID
    int size; // Memory requirement of the job

    Job(int id, int size) {
        this.id = id;
        this.size = size;
    }
}

public class MemoryAllocation {
    
    // First Fit Memory Allocation
    static void firstFit(List<Partition> partitions, List<Job> jobs) {
        for (Job job : jobs) {
            for (Partition partition : partitions) {
                if (!partition.occupied && partition.size >= job.size) {
                    partition.occupied = true;
                    partition.jobId = job.id;
                    break; // Allocate and move to next job
                }
            }
        }
        printMemoryState("First Fit", partitions, jobs);
    }

    // Best Fit Memory Allocation
    static void bestFit(List<Partition> partitions, List<Job> jobs) {
        for (Job job : jobs) {
            Partition best = null;
            for (Partition partition : partitions) {
                if (!partition.occupied && partition.size >= job.size) {
                    if (best == null || partition.size < best.size) {
                        best = partition;
                    }
                }
            }
            if (best != null) {
                best.occupied = true;
                best.jobId = job.id;
            }
        }
        printMemoryState("Best Fit", partitions, jobs);
    }

    // Worst Fit Memory Allocation
    static void worstFit(List<Partition> partitions, List<Job> jobs) {
        for (Job job : jobs) {
            Partition worst = null;
            for (Partition partition : partitions) {
                if (!partition.occupied && partition.size >= job.size) {
                    if (worst == null || partition.size > worst.size) {
                        worst = partition;
                    }
                }
            }
            if (worst != null) {
                worst.occupied = true;
                worst.jobId = job.id;
            }
        }
        printMemoryState("Worst Fit", partitions, jobs);
    }

    // Next Fit Memory Allocation
    static void nextFit(List<Partition> partitions, List<Job> jobs) {
        int lastIndex = 0;
        for (Job job : jobs) {
            int i = lastIndex;
            do {
                Partition partition = partitions.get(i);
                if (!partition.occupied && partition.size >= job.size) {
                    partition.occupied = true;
                    partition.jobId = job.id;
                    lastIndex = i;
                    break;
                }
                i = (i + 1) % partitions.size();
            } while (i != lastIndex);
        }
        printMemoryState("Next Fit", partitions, jobs);
    }

    // Method to print memory state and calculate fragmentation
    static void printMemoryState(String method, List<Partition> partitions, List<Job> jobs) {
        System.out.println("\n" + method + " Allocation:");
    
        int internalFragmentation = 0;
        int totalFreeSpace = 0;
        boolean canFitAnyJob = false;

        for (Partition partition : partitions) {
            String jobStr = (partition.jobId == -1) ? "None" : String.valueOf(partition.jobId);
            System.out.println("Partition: Size=" + partition.size + " Occupied=" + partition.occupied + " JobID=" + jobStr);

            if (partition.occupied) {
                // Calculate internal fragmentation (unused space in allocated partitions)
                internalFragmentation += (partition.size - findJobSize(partition.jobId, jobs));
            } else {
                // Accumulate free space in unallocated partitions
                totalFreeSpace += partition.size;
            }
        }

        // Check if any free partition can fit at least one waiting job
        for (Job job : jobs) {
            if (jobNotAllocated(job.id, partitions)) {
                for (Partition partition : partitions) {
                    if (!partition.occupied && partition.size >= job.size) {
                        canFitAnyJob = true;
                        break;
                    }
                }
            }
        }

        // External fragmentation calculation
        int externalFragmentation = (totalFreeSpace > 0 && !canFitAnyJob) ? totalFreeSpace : 0;

        // Print fragmentation info
        System.out.println("Internal Fragmentation: " + internalFragmentation);
        System.out.println("External Fragmentation: " + externalFragmentation);
    }

    // Helper function to find job size based on Job ID
    static int findJobSize(int jobId, List<Job> jobs) {
        for (Job job : jobs) {
            if (job.id == jobId) return job.size;
        }
        return 0; // If no job is assigned, return 0
    }

    // Helper function to check if a job is not allocated
    static boolean jobNotAllocated(int jobId, List<Partition> partitions) {
        for (Partition partition : partitions) {
            if (partition.jobId == jobId) {
                return false; // Job is already allocated
            }
        }
        return true;
    }

    // Deep copy function to preserve original partition data for each allocation method
    static List<Partition> deepCopyPartitions(List<Partition> original) {
        List<Partition> copy = new ArrayList<>();
        for (Partition p : original) {
            copy.add(new Partition(p.size)); // Create a new Partition object
        }
        return copy;
    }

    // Main method to test allocation strategies
    public static void main(String[] args) {
        List<Partition> partitions = Arrays.asList(
            new Partition(100), new Partition(500), 
            new Partition(200), new Partition(300), new Partition(600)
        );
    
        List<Job> jobs = Arrays.asList(
            new Job(1, 212), new Job(2, 417), 
            new Job(3, 112), new Job(4, 426)
        );

        // Running different allocation strategies
        firstFit(deepCopyPartitions(partitions), new ArrayList<>(jobs));
        bestFit(deepCopyPartitions(partitions), new ArrayList<>(jobs));
        worstFit(deepCopyPartitions(partitions), new ArrayList<>(jobs));
        nextFit(deepCopyPartitions(partitions), new ArrayList<>(jobs));
    }
}
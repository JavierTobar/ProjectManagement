export interface Project {
    project: string;
    description: string;
    startDate: string;
    devsLimit: number;
    criticalPath: number;
    employees: Employee[];
}

export interface Task {
    taskID: string;
    description: string;
    project_fk: string;
    employee_fk: string;
    predesessorTaskID: string;
    successorTaskID: string;
    duration: number;
    optimisticWeight: number;
    realisticWeight: number;
    pessimisticWeight: number;
    optimisticEstimation: number;
    realisticEstimation: number;
    pessimisticEstimation: number;
}

export interface EmployeePostDTO {
    name: string;
}

export interface EmployeeGetDTO {
    employeeID: number;
    name: string;
    tasks: Task[];
    projectnames: string[];
}
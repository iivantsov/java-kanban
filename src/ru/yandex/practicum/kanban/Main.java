package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.Managers;
import ru.yandex.practicum.kanban.service.api.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        System.out.println("Kanban App Demo:\n");
        String testStage1Log = "1. CREATE @ ";

        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(30);

        Task hobbyTask = new Task("Violin", "Try to play Vivaldi \"The Four Seasons\"",
                startTime, duration);
        Integer hobbyTaskID = taskManager.createTask(hobbyTask);
        startTime = startTime.plus(duration);
        testStage1Log += "Task id=" + hobbyTaskID + ", ";

        Task houseTask = new Task("Cleaning", "Vacuum and wash the floors at home",
                startTime, duration);
        Integer houseTaskID = taskManager.createTask(houseTask);
        startTime = startTime.plus(duration);
        testStage1Log += "Task id=" + houseTaskID + ", ";

        Epic workEpic = new Epic("Kanban App", "Working on Kanban App for this week");
        Integer workEpicID = taskManager.createEpic(workEpic);
        testStage1Log += "Epic id=" + workEpicID + ", ";

        Subtask workSubtask1 = new Subtask("Presentation", "Prepare a basic presentation layout",
                startTime, duration);
        workSubtask1.setEpicID(workEpicID);
        Integer workSubtask1ID = taskManager.createSubtask(workSubtask1);
        startTime = startTime.plus(duration);
        testStage1Log += "Subtask id=" + workSubtask1ID + ", ";

        Subtask workSubtask2 = new Subtask("Test report", "Prepare a backend test report",
                startTime, duration);
        workSubtask2.setEpicID(workEpicID);
        Integer workSubtask2ID = taskManager.createSubtask(workSubtask2);
        startTime = startTime.plus(duration);
        testStage1Log += "Subtask id=" + workSubtask2ID + ", ";

        Subtask workSubtask3 = new Subtask("Bonus", "Prepare a list of developers for bonus payment",
                startTime, duration);
        workSubtask3.setEpicID(workEpicID);
        Integer workSubtask3ID = taskManager.createSubtask(workSubtask3);
        startTime = startTime.plus(duration);
        testStage1Log += "Subtask id=" + workSubtask3ID + ", ";

        Epic educationEpic = new Epic("Education", "List of education tasks for the week");
        Integer educationEpicID = taskManager.createEpic(educationEpic);
        testStage1Log += "Epic id=" + educationEpicID + ", ";

        Subtask educationSubtask1 = new Subtask("Chinese", "Translate a few short poems",
                startTime, duration);
        educationSubtask1.setEpicID(educationEpicID);
        Integer educationSubtask1ID = taskManager.createSubtask(educationSubtask1);
        testStage1Log += "Subtask id=" + educationSubtask1ID;

        printTaskManagerTestReport(taskManager, testStage1Log);

        // HISTORY
        taskManager.getTaskByID(hobbyTaskID);
        taskManager.getTaskByID(houseTaskID);
        taskManager.getEpicByID(workEpicID);
        taskManager.getSubtaskByID(workSubtask1ID);
        taskManager.getSubtaskByID(workSubtask2ID);
        taskManager.getSubtaskByID(workSubtask3ID);
        taskManager.getEpicByID(educationEpicID);
        taskManager.getSubtaskByID(workSubtask3ID);
        taskManager.getSubtaskByID(workSubtask2ID);
        taskManager.getSubtaskByID(workSubtask1ID);
        taskManager.getEpicByID(educationEpicID);
        taskManager.getTaskByID(hobbyTaskID);

        printHistory(taskManager);

        String testStage2Log = "2. UPDATE @ ";

        hobbyTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(hobbyTask);
        testStage2Log += "Task id=" + hobbyTaskID + "(" + hobbyTask.getStatus() + "), ";

        houseTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(houseTask);
        testStage2Log += "Task id=" + houseTaskID + "(" + houseTask.getStatus() + "), ";

        workSubtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(workSubtask1);
        testStage2Log += "Subtask id=" + workSubtask1ID + "(" + workSubtask1.getStatus() + "), ";

        educationSubtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(educationSubtask1);
        testStage2Log += "Subtask id=" + educationSubtask1ID + "(" + educationSubtask1.getStatus() + "), ";

        printTaskManagerTestReport(taskManager, testStage2Log);

        String testStage3Log = "3. REMOVE @ ";

        taskManager.removeTaskByID(houseTaskID);
        testStage3Log += "Task id=" + houseTaskID + ", ";

        taskManager.removeEpicByID(workEpicID);
        testStage3Log += "Epic id=" + workEpicID + ", ";

        printTaskManagerTestReport(taskManager, testStage3Log);
        printHistory(taskManager);
    }

    private static void printTaskManagerTestReport(TaskManager taskManager, String testStageLog) {
        System.out.println(testStageLog + "\n");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println();

        for (Epic epic : taskManager.getAllEpic()) {
            System.out.println(epic);
        }
        System.out.println();

        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println();
    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("@ HISTORY");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}
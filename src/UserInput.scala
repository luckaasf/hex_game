import HexGame.Board
import Player.{HumanPlayer, MachinePlayer}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object UserInput {

  @tailrec
  def gameInitialConfig(): (Int, Board, MyRandom, List[Board], HumanPlayer, MachinePlayer) = {
    val (humanPlayer, machinePlayer) = askForHumanColor()
    Display.askForInput(1)
    Try(scala.io.StdIn.readInt()) match {
      case Success(boardSize) =>
        val board: Board = BoardUtils.createBoard(boardSize)
        val boardStates: List[Board] = board :: List()
        val random = MyRandom(999999)
        (boardSize, board, random, boardStates, humanPlayer, machinePlayer)
      case Failure(_: Throwable) =>
        Display.showError(1)
        gameInitialConfig()
    }
  }

  @tailrec
  private def askForHumanColor(): (HumanPlayer, MachinePlayer) = {
    Display.askForInput(4)
    val input = scala.io.StdIn.readLine.toUpperCase()
    input match {
      case "B" =>
        val humanPlayer = HumanPlayer(Cells.Blue)
        val machinePlayer = MachinePlayer(Cells.oppositeCell(humanPlayer.color))
        (humanPlayer, machinePlayer)
      case "R" =>
        val humanPlayer = HumanPlayer(Cells.Red)
        val machinePlayer = MachinePlayer(Cells.oppositeCell(humanPlayer.color))
        (humanPlayer, machinePlayer)
      case _ =>
        Display.showError(4)
        askForHumanColor()
    }
  }

  @tailrec
  def inputPlayer(board: Board): (Int, Int) = {
    Display.askForInput(2)
    val input = scala.io.StdIn.readLine()
    val values = input.split(" ")
    Try((values(0).toInt, values(1).toInt)) match {
      case Success((row, col)) =>
        if (GeneralUtils.isValid(board, row, col)) {
          (row, col)
        } else {
          inputPlayer(board)
        }
      case Failure(_: Throwable) =>
        Display.showError(3)
        inputPlayer(board)
    }

  }
}

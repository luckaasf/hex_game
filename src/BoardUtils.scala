import HexGame.Board

import scala.annotation.tailrec

object BoardUtils {

  def createBoard(boardSize: Int): Board = List.fill(boardSize)(List.fill(boardSize)(Cells.Empty))

  @tailrec
  def displayBoard(board: Board, row: Int = 0, col: Int = 0): Unit = {
    if (row == 0 && col == 0) {
      Display.displayHeader(board)
    }
    if (row < board.length) {
      val spacing = " " * (row * 2)
      if (col == 0) {
        Display.displayRowConfig(board, 1, row, spacing)
      }
      val cell = board(row)(col)
      if (col != 0) {
        Display.displayObjects(1)
      }
      cell match {
        case Cells.Empty => Display.displayObjects(2)
        case Cells.Blue => Display.displayObjects(3)
        case Cells.Red => Display.displayObjects(4)
      }

      if (col == board.length - 1) {
        Display.displayRowConfig(board, 2, row, spacing)
        if (row != board.length - 1) {
          Display.displayRowConfig(board, 3, row, spacing)
        }
        if (row == board.length - 1) {
          Display.displayFooter(board)
        }
        displayBoard(board, row + 1)
      } else {
        displayBoard(board, row, col + 1)
      }
    }
  }

  @tailrec
  def isBoardFull(board: Board): Boolean = {
    board match {
      case Nil => true
      case row :: tail =>
        if (row.contains(Cells.Empty))
          false
        else
          isBoardFull(tail)
    }
  }

}

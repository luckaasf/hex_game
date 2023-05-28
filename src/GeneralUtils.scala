import HexGame.Board

object GeneralUtils {

  def isValid(board: Board, row: Int, col: Int): Boolean = {
    if (isCoordinateValid(board, row, col) && board(row)(col) == Cells.Empty) {
      true
    } else {
      Display.showError(2)
      false
    }
  }
  private def isCoordinateValid(board: Board, row: Int, col: Int): Boolean = row >= 0 && row <= board.length - 1 && col >= 0 && col <= board.length - 1

  def getNeighbors(board: Board, row: Int, col: Int): List[(Int, Int)] = {
    val lst: List[(Int, Int)] = List((row, col - 1), (row - 1, col), (row - 1, col + 1), (row, col + 1), (row + 1, col - 1), (row + 1, col))

    def loop(lst: List[(Int, Int)]): List[(Int, Int)] = {
      lst match {
        case Nil => Nil
        case x :: xs => if (GeneralUtils.isCoordinateValid(board, x._1, x._2)) x :: loop(xs) else loop(xs)
      }
    }

    loop(lst)
  }
}

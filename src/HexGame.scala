import HexGame.Board
import Player.{HumanPlayer, MachinePlayer}

import scala.annotation.tailrec

case class HexGame(boardSize: Int) {
  def runGame(board: Board, player: Cells.Cell, rand: MyRandom, boardStates: List[Board], humanPlayer: HumanPlayer, machinePlayer: MachinePlayer): Unit = {
    HexGame.runGame(board, player, rand, boardStates, humanPlayer, machinePlayer)
  }

  def undo(boardState: List[Board]): (Board, List[Board]) = {
    HexGame.undo(boardState)
  }

  def play(board: Board, player: Cells.Cell, row: Int, col: Int): Board = {
    HexGame.play(board, player, row, col)
  }

  def randomMove(board: Board, rand: RandomState): ((Int, Int), RandomState) = {
    HexGame.randomMove(board, rand)
  }

  def hasContiguousLine(board: Board, player: Cells.Cell): (Boolean, Cells.Cell) = {
    HexGame.hasContiguousLine(board, player)
  }
}

object HexGame {

  type Board = List[List[Cells.Cell]]

  @tailrec
  private def runGame(board: Board, currentPlayer: Cells.Cell, rand: RandomState, boardStates: List[Board], humanPlayer: HumanPlayer, machinePlayer: MachinePlayer): Unit = {
    if (BoardUtils.isBoardFull(board)) {
      BoardUtils.displayBoard(board)
      Display.displayTie()
      sys.exit()
    } else {
      if (currentPlayer == humanPlayer.color) {
        BoardUtils.displayBoard(board)
        Display.askForInput(3)
        val undo = scala.io.StdIn.readLine()
        if (undo.equals("Y") || undo.equals("y")) {
          val (newBoard, newBoardState) = HexGame.undo(boardStates)
          runGame(newBoard, humanPlayer.color, rand, newBoardState, humanPlayer, machinePlayer)
        } else {
          val (row, col) = UserInput.inputPlayer(board)
          val newBoard = play(board, currentPlayer, row, col)
          if (hasContiguousLine(newBoard, currentPlayer)._1) {
            BoardUtils.displayBoard(newBoard)
            Display.announceWinner(currentPlayer, humanPlayer, machinePlayer)
            sys.exit()
          }
          runGame(newBoard, machinePlayer.color, rand, newBoard :: boardStates, humanPlayer, machinePlayer)
        }
      } else {
        val ((row, col), newRand) = randomMove(board, rand)
        val newBoard = play(board, currentPlayer, row, col)
        if (hasContiguousLine(newBoard, currentPlayer)._1) {
          BoardUtils.displayBoard(newBoard)
          Display.announceWinner(currentPlayer, humanPlayer, machinePlayer)
          sys.exit()
        }
        runGame(newBoard, humanPlayer.color, newRand, newBoard :: boardStates, humanPlayer, machinePlayer)
      }
    }
  }

  def undo(boardState: List[Board]): (Board, List[Board]) = {
    boardState match {
      case Nil => (Nil, boardState)
      case currentState :: Nil => (currentState, boardState) // Assim se o jogador fizer muitos undos ele volta ao estado inicial do jogo e é o máximo que pode chegar
      case _ :: _ :: previousState :: tail => (previousState, previousState :: tail)
    }
  }

  // O método play faz o update no estado antigo do board e devolve o novo estado do jogo
  def play(board: Board, player: Cells.Cell, row: Int, col: Int): Board = {
    board.updated(row, board(row).updated(col, player)) // não tenho a certeza se o metodo updated é puro (perguntar na discussão)
  }

  private def emptyCells(board: Board): List[(Int, Int)] = {
    @tailrec
    def loop(row: Int, col: Int, acc: List[(Int, Int)]): List[(Int, Int)] = {
      if (row >= board.length) { // a linha atual é maior que o tamanho do board, ou seja, deveria retornar o acumulador
        acc.reverse
      } else if (col >= board(row).length) { // se a coluna for maior que o tamanho da linha atual, então é a ultima coluna da linha portanto deve passar para a proxima linha (e começa na coluna 0)
        loop(row + 1, 0, acc)
      } else { // o resto dos casos, ou seja, as celulas ainda precisam ser avaliadas
        board(row)(col) match {
          case Cells.Empty => loop(row, col + 1, (row, col) :: acc)
          case _ => loop(row, col + 1, acc)
        }
      }
    }

    loop(0, 0, Nil)
  }

  def randomMove(board: Board, rand: RandomState): ((Int, Int), RandomState) = {
    val returnedList = emptyCells(board)
    val (index, newRand) = rand.nextInt(returnedList.length)
    Display.showInfo(returnedList(index)._1, returnedList(index)._2)
    (returnedList(index), newRand)
  }

  def hasContiguousLine(board: Board, player: Cells.Cell): (Boolean, Cells.Cell) = {
    if (player == Cells.Blue) {
      (board.indices
        .filter { col => board.head(col) == Cells.Blue }
        .exists { col => isBlueWinner(board, 0, col, List((0, col))) }, player)
    } else {
      (board.indices
        .filter { row => board(row).head == Cells.Red }
        .exists { row => isRedWinner(board, row, 0, List((row, 0))) }, player)
    }
  }

  private def isBlueWinner(board: Board, row: Int, col: Int, visited: List[(Int, Int)]): Boolean = {
    val cell = board(row)(col)
    if (row == board.size - 1 && cell == Cells.Blue) {
      true
    } else if (cell == Cells.Blue) {
      val neighbors = GeneralUtils.getNeighbors(board, row, col)
      neighbors
        .filter { case (row, col) => !visited.contains((row, col)) && board(row)(col) == Cells.Blue }
        .exists { case (row, col) => isBlueWinner(board, row, col, (row, col) :: visited) }
    } else {
      false
    }
  }

  private def isRedWinner(board: Board, row: Int, col: Int, visited: List[(Int, Int)]): Boolean = {
    val cell = board(row)(col)
    if (col == board.head.size - 1 && cell == Cells.Red) {
      true
    } else if (cell == Cells.Red) {
      val neighbors = GeneralUtils.getNeighbors(board, row, col)
      neighbors
        .filter { case (row, col) => !visited.contains((row, col)) && board(row)(col) == Cells.Red }
        .exists { case (row, col) => isRedWinner(board, row, col, (row, col) :: visited) }
    } else {
      false
    }
  }
}

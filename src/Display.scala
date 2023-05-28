import HexGame.Board
import Player.{HumanPlayer, MachinePlayer}

object Display {

  def startMessage(): Unit = {
    println("Welcome to my Hex Game!\n")
  }

  def askForInput(code: Int): Unit = {
    code match {
      case 1 => print("Select a size for the board, e.g: 5 -> ")
      case 2 => print("Choose a column (separated by spaces) e.g: 0 1 -> ")
      case 3 => print("Undo a move? (Y/N) -> ")
      case 4 => print("Choose a color (Red: R/Blue: B) -> ")
    }
  }

  def showInfo(row: Int, col: Int): Unit = {
    println("The machine has chosen " + "(" + row + ", " + col + ")")
  }

  def displayTie(): Unit = {
    println(Console.YELLOW + "It's a tie!" + Console.RESET)
  }

  def displayTieGUI(): String = {
    "It's a tie!"
  }

  def announceWinner(winner: Cells.Cell, humanPlayer: HumanPlayer, machinePlayer: MachinePlayer): Unit = {
    winner match {
      case Cells.Blue =>
        winner match {
          case humanPlayer.color => println(Console.BLUE + "You won the game!" + Console.RESET)
          case machinePlayer.color => println(Console.BLUE + "The machine won the game!" + Console.RESET)
        }
      case Cells.Red =>
        winner match {
          case humanPlayer.color => println(Console.RED + "You won the game!" + Console.RESET)
          case machinePlayer.color => println(Console.RED + "The machine won the game!" + Console.RESET)
        }
    }
  }

  def announceWinnerGUI(winner: Cells.Cell, humanPlayer: HumanPlayer, machinePlayer: MachinePlayer): String = {
    winner match {
      case Cells.Blue => winner match {
        case humanPlayer.color => "You won the game!"
        case machinePlayer.color => "The machine won the game!"
      }
      case Cells.Red =>
        winner match {
          case humanPlayer.color => "You won the game!"
          case machinePlayer.color => "The machine won the game!"
        }
    }
  }

  def showInfoGUI(code: Int, row: Int, col: Int): String = {
    code match {
      case 1 => "This position is occupied, choose another one"
      case 2 => "The machine has chosen (" + row + ", " + col + ")"
      case 3 => "You have to choose a color in order to proceed!"
      case 4 => "Game Over!"
    }

  }

  def showError(code: Int): Unit = {
    code match {
      case 1 => println(Console.RED + "Invalid size! the board size has to be a number, e.g, 5" + Console.RESET)
      case 2 => println(Console.RED + "Invalid move! choose a position within the board" + Console.RESET)
      case 3 => println(Console.RED + "Invalid move! It can't be a string or more than a number" + Console.RESET)
      case 4 => println(Console.RED + "Invalid color! If you want to be the blue player choose B, otherwise choose R for the red color" + Console.RESET)
    }
  }

  def displayHeader(board: Board): Unit = {
    print("   ")
    board.indices.map(col => print(Console.BLUE + col + " " * 3 + Console.RESET))
    println()
    print("   ")
    board.indices.map(_ => print(Console.BLUE + "*" + " " * 3 + Console.RESET))
    println()
  }

  def displayFooter(board: Board): Unit = {
    print(" " * (board.length * 2 + 3))
    print(Console.BLUE + "*   " * board.length + Console.RESET)
    println()
  }

  def displayRowConfig(board: Board, code: Int, row: Int, spacing: String): Unit = {
    code match {
      // add the number of the current row and a "*" right after (with a spacing offset between them)
      case 1 => print(Console.RED + row + spacing + " *" + Console.RESET)
      // add a "*" after the last column of each row
      case 2 =>
        print(Console.RED + " *" + Console.RESET)
        println()
      // add \ / \ / ... \ below each row, this config doesnt apply for the last row
      case 3 =>
        print(spacing + "     " + "\\ / " * (board.length - 1) + "\\")
        println()
    }
  }

  def displayObjects(code: Int): Unit = {
    code match {
      case 1 => print(" -")
      case 2 => print(" " + ".") // empty cell
      case 3 => print(" " + Console.BLUE + "O" + Console.RESET) // human cell
      case 4 => print(" " + Console.RED + "X" + Console.RESET) // machine cell
    }
  }
}

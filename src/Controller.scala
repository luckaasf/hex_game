import HexGame.Board

import Player.{HumanPlayer, MachinePlayer}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Alert, Button, ChoiceDialog, Label}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color

import java.net.URL
import java.util.ResourceBundle
import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

class Controller extends Initializable {

  private val GAME_SIZE: Int = 5

  private val hexGame: HexGame = HexGame(GAME_SIZE)
  private var myRandom: RandomState = MyRandom(999999)
  private var currentBoard = BoardUtils.createBoard(hexGame.boardSize)
  private var boardStates: List[Board] = _
  private var humanPlayer: HumanPlayer = _
  private var machinePlayer: MachinePlayer = _

  @FXML
  private var machineLabel: Label = _

  @FXML
  private var anchorPane: AnchorPane = _

  @FXML def cellClicked(event: MouseEvent): Unit = {
    val sourceButton = event.getSource.asInstanceOf[Button]
    val buttonId = sourceButton.getId

    val coordinates = buttonId.split("_")
    val row = coordinates(1).toInt
    val col = coordinates(2).toInt

    val backgroundColor = sourceButton.getBackground.getFills.get(0).getFill
    if (backgroundColor == Color.RED || backgroundColor == Color.BLUE) {
      val alert = new Alert(Alert.AlertType.WARNING)
      alert.setTitle("Warning!")
      alert.setHeaderText(null)
      alert.setContentText(Display.showInfoGUI(1, row, col))
      alert.showAndWait()
    } else {
      handleHumanPlay(row, col)
      if (BoardUtils.isBoardFull(currentBoard)) {
        updateGUI(currentBoard)
        val alert = new Alert(Alert.AlertType.CONFIRMATION)
        alert.setTitle("Game Over!")
        alert.setHeaderText(null)
        alert.setContentText(Display.displayTieGUI())
        alert.showAndWait()
      } else {
        handleMachinePlay()
        updateGUI(currentBoard)
      }
    }
  }

  @FXML def onClickUndo(): Unit = {
    if (BoardUtils.isBoardFull(currentBoard) || hexGame.hasContiguousLine(currentBoard, humanPlayer.color)._1 || hexGame.hasContiguousLine(currentBoard, machinePlayer.color)._1) {
      val alert = new Alert(Alert.AlertType.CONFIRMATION)
      alert.setTitle("Game Over!")
      alert.setContentText(Display.showInfoGUI(4, 0, 0))
      alert.showAndWait()
    } else {
      val (newCurrentBoard, newBoardStates) = hexGame.undo(boardStates)
      currentBoard = newCurrentBoard
      boardStates = newBoardStates
      machineLabel.setText("")
      updateGUI(currentBoard)
    }
  }

  private def updateGUI(board: Board): Unit = {
    @tailrec
    def loop(row: Int, col: Int): Unit = {
      if (row < hexGame.boardSize && col < hexGame.boardSize) {
        board(row)(col) match {
          case Cells.Empty => updateButton(Cells.Empty, row, col)
          case Cells.Blue => updateButton(Cells.Blue, row, col)
          case Cells.Red => updateButton(Cells.Red, row, col)
        }
        loop(row, col + 1)
      } else if (row < hexGame.boardSize) {
        loop(row + 1, 0)
      }
    }
    loop(0, 0)
  }

  private def handleHumanPlay(row: Int, col: Int): Unit = {
    currentBoard = hexGame.play(currentBoard, humanPlayer.color, row, col)
    boardStates = currentBoard :: boardStates

    if (hexGame.hasContiguousLine(currentBoard, humanPlayer.color)._1) {
      val alert = new Alert(Alert.AlertType.CONFIRMATION)
      alert.setContentText(Display.announceWinnerGUI(humanPlayer.color, humanPlayer, machinePlayer))
      alert.showAndWait()
    }
  }

  private def handleMachinePlay(): Unit = {
    val ((machineRow, machineCol), newRandom) = HexGame.randomMove(currentBoard, myRandom)
    myRandom = newRandom
    currentBoard = HexGame.play(currentBoard, machinePlayer.color, machineRow, machineCol)
    boardStates = currentBoard :: boardStates
    machineLabel.setText(Display.showInfoGUI(2, machineRow, machineCol))

    if (hexGame.hasContiguousLine(currentBoard, machinePlayer.color)._1) {
      val alert = new Alert(Alert.AlertType.CONFIRMATION)
      alert.setContentText(Display.announceWinnerGUI(machinePlayer.color, humanPlayer, machinePlayer))
      alert.showAndWait()
    }
  }
  private def findButtonById(buttonIdPattern: String): Option[Button] = {
    val buttons = anchorPane.getChildrenUnmodifiable
      .filtered(_.isInstanceOf[Button])
      .asInstanceOf[java.util.List[Button]]

    buttons
      .asScala
      .find(_.getId == buttonIdPattern)
  }

  private def updateButton(color: Cells.Cell, row: Int, col: Int): Unit = {
    val buttonIdPattern = "button_" + row + "_" + col
    val Some(buttonToUpdate) = findButtonById(buttonIdPattern)

    color match {
      case Cells.Blue =>
        buttonToUpdate.setStyle("-fx-background-color: blue;")
        buttonToUpdate.setText("")
      case Cells.Red =>
        buttonToUpdate.setStyle("-fx-background-color: red;")
        buttonToUpdate.setText("")
      case Cells.Empty =>
        buttonToUpdate.setStyle("")
        buttonToUpdate.setText(".")
    }
  }
  @tailrec
  private def playerConfiguration(): Unit = {
    val dialog = new ChoiceDialog[String]("--Choose a color--", "Blue", "Red")
    dialog.setTitle("Initial configuration")
    dialog.setHeaderText("Choose a color")
    dialog.setContentText("Color:")

    val result: Option[String] = Option(dialog.showAndWait().orElse(""))
    result match {
      case Some("Blue") =>
        humanPlayer = HumanPlayer(Cells.Blue)
        machinePlayer = MachinePlayer(Cells.oppositeCell(humanPlayer.color))
      case Some("Red") =>
        humanPlayer = HumanPlayer(Cells.Red)
        machinePlayer = MachinePlayer(Cells.oppositeCell(humanPlayer.color))
      case Some("--Choose a color--") =>
        val alert = new Alert(Alert.AlertType.WARNING)
        alert.setTitle("Warning")
        alert.setHeaderText(null)
        alert.setContentText(Display.showInfoGUI(3, 0, 0))
        alert.showAndWait()
        playerConfiguration()
      case _ =>
        sys.exit()
    }
  }

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    playerConfiguration()
    boardStates = currentBoard :: List()
  }
}

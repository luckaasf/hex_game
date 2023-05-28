object Player {

  sealed trait Player {
    val color: Cells.Cell
  }

  case class HumanPlayer(color: Cells.Cell) extends Player

  case class MachinePlayer(color: Cells.Cell) extends Player
}

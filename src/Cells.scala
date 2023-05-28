object Cells extends Enumeration {
  type Cell = Value
  val Red, Blue, Empty = Value

  def oppositeCell(cell: Cell): Cell = {
    cell match {
      case Blue => Red
      case Red => Blue
    }
  }
}

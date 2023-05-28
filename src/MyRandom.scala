trait RandomState {
  def nextInt: (Int, RandomState)
  def nextInt(n: Int): (Int, RandomState)
}

case class MyRandom(seed: Long) extends RandomState {

  def nextInt: (Int, RandomState) = {
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val nextRandom = MyRandom(newSeed)
    val n = (newSeed >>> 16).toInt
    (n, nextRandom)
  }

  def nextInt(n: Int): (Int, RandomState) = {
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val nextRandom = MyRandom(newSeed)
    val nn = (newSeed >>> 16).toInt % n
    (if (nn < 0) -nn else nn, nextRandom)
  }

}

package digital.lamp.mindlamp.utils

fun <T> Sequence<T>.batch(n: Int): Sequence<List<T>> {
    return BatchingSequence(this, n)
}

private class BatchingSequence<T>(val source: Sequence<T>, val batchSize: Int) : Sequence<List<T>> {
    override fun iterator(): Iterator<List<T>> = object : AbstractIterator<List<T>>() {
        val iterate = source.iterator()

        override fun computeNext() {
            if (iterate.hasNext()) {
                val batch = mutableListOf<T>()
                repeat(batchSize) {
                    if (iterate.hasNext()) {
                        batch.add(iterate.next())
                    } else {
                        return@repeat
                    }
                }
                setNext(batch)
            } else {
                done()
            }
        }
    }
}

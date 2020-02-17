

###InvalidateView3

        onDraw执行次数明显少于invalidate执行次数



###InvalidateView2

        刷新放到了子线程，并且sleep了，基本上是可以，效果错误应该是计算的问题，不是刷新的问题



###InvalidateView

        通过Handler解决了，原理？


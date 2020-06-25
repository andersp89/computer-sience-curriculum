
; Tail recursion

(define (replicate x n)
  (define (replicate-tail num lst)
  (if (= num 0)
    lst
    (replicate-tail (- num 1) (cons x lst)))
  )
  (replicate-tail n nil)
  )

(define (accumulate combiner start n term)
  (cond 
    ((= n 0) start)
    (else 
      (combiner (term n) (accumulate combiner start (- n 1) term))))
)

(define (accumulate-tail combiner start n term)
  (define (accumulate-tail-tail n result)
    (cond
      ((= n 0) result)
      (else
        (accumulate-tail-tail (- n 1) (combiner (term n) result)))))
  (accumulate-tail-tail n start)
)

; Streams

(define (map-stream f s)
    (if (null? s)
    	nil
    	(cons-stream (f (car s)) (map-stream f (cdr-stream s)))))

(define multiples-of-three
  (cons-stream 3 (map-stream (lambda (num) (+ num 3)) multiples-of-three))
)

(define a (cons-stream 1 (cons-stream 2 (cons-stream 3 nil))))

(define (nondecreastream s)
  (cond 
    ((null? s) nil)
    ((or (null? (cdr-stream s)) (< (car (cdr-stream s)) (car s)))
      (cons-stream (list (car s)) (nondecreastream (cdr-stream s))))
    (else 
      (cons-stream (cons (car s) (car (nondecreastream (cdr-stream s)))) (cdr-stream (nondecreastream (cdr-stream s))))))
    )


(define finite-test-stream
    (cons-stream 1
        (cons-stream 2
            (cons-stream 3
                (cons-stream 1
                    (cons-stream 2
                        (cons-stream 2
                            (cons-stream 1 nil))))))))

(define infinite-test-stream
    (cons-stream 1
        (cons-stream 2
            (cons-stream 2
                infinite-test-stream))))
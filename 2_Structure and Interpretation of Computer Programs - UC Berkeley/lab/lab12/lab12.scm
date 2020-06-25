(define (partial-sums stream)
  (define (helper last-element stream)
  	(cond 
  		((null? stream) nil)
  		((= last-element 0) (cons-stream (car stream) (helper (car stream) (cdr-stream stream))))
  		(else (cons-stream (+ (car stream) last-element) (helper (+ (car stream) last-element) (cdr-stream stream)))))
  )
  (helper 0 stream)
  )
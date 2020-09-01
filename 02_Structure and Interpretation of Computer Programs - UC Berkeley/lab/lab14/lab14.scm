; Lab 14: Final Review

(define (compose-all funcs)
  'YOUR-CODE-HERE
  (define (return-func num)
  	(define (do-work funcz total)
  	  (cond 
  		((null? funcz) total)
  		(else 
  		  (do-work (cdr funcz) ((car funcsz) total)))))
  	(do-work funcs num))
   	return-func
 )

(define (has-cycle? s)
  (define (pair-tracker seen-so-far curr)
    (cond ((null? curr) #f)
          ((contains? seen-so-far (car curr)) #t)
          (else (pair-tracker (cons (car curr) seen-so-far) (cdr-stream curr)))))
  (pair-tracker nil s))

(define (contains? lst s)
  'YOUR-CODE-HERE
    (cond
      ((null? lst) #f)
      ((eq? s (car lst)) #t)
      (else
       (contains? (cdr lst) s))
    )
)


" Replace with your own values
let s:token = "<your token here>"
let s:project_id = "<your project id here>"

let s:access_args = " --token " . s:token . " --project-id " . s:project_id
let s:p_dir = expand('<sfile>:p:h')
let s:jar_pre = 'java -jar ' . s:p_dir . '/../pt.jar'
let s:lein_pre = 'lein run'

function! <SID>LoadStories()
    "exec('!java -jar ' . s:p_dir . '/../pt.jar --load-stories --query label\%3A\%22t_dev\%20a\%22')
    set autoread
    call inputsave()
    let query = input('Enter search query: ')
    call inputrestore()
    let cmd = s:lein_pre . s:access_args . ' --load-stories --query ' . shellescape(query)
    exec('!echo "' . cmd . '"; ' . cmd)
    exec(':e /tmp/stories.txt')
    nnoremap <buffer> <leader>ptw :call <SID>SaveStory()<cr>
endfunction

nnoremap <leader>pte :call <SID>LoadStories()<cr>

function! <SID>SaveStory()
    w
    "norm byw
    let wordUnderCursor = expand("<cword>")
    let cmd = s:lein_pre . s:access_args . ' --save-story ' . wordUnderCursor "@0
    exec('!echo "' . cmd . '"; ' . cmd)
    e
endfunction

nnoremap <leader>pts :source ~/.vim/bundle/vim-pivotal-tracker/plugin/pivotal.vim<CR>
nnoremap <leader>ptj :!lein uberjar<CR>

echo "pivotal script loaded!"
